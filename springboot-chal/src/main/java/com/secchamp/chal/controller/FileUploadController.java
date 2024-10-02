package com.secchamp.chal.controller;

import com.secchamp.chal.config.FileTypeWhitelistConfig;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${clamav.host}")
    private String clamavHost;

    @Value("${clamav.port}")
    private int clamavPort;

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${upload.maxFileSize}")
    private long maxFileSize;

    @Autowired
    private FileTypeWhitelistConfig fileTypeWhitelistConfig;

    @PostMapping("/pages/upload/no-check")
    public String uploadWithoutCheck(@RequestParam("file") MultipartFile file, Model model) {
        logger.info("Uploading file without checks: {}", file.getOriginalFilename());
        try {
            String filePath = saveFile(file);
            String fileUrl = "/uploads/" + file.getOriginalFilename();
            model.addAttribute("filePath", fileUrl);
            model.addAttribute("message", "File uploaded successfully without checks: " + file.getOriginalFilename());
            return "public/upload";
        } catch (IOException e) {
            logger.error("Failed to upload file without checks", e);
            model.addAttribute("message", "Failed to upload file: " + e.getMessage());
            return "public/upload";
        }
    }

    @PostMapping("/pages/upload/with-check")
    public String uploadWithCheck(@RequestParam("file") MultipartFile file, Model model) {
        logger.info("Uploading file with checks: {}", file.getOriginalFilename());
        String fileType = null;
        String fileExtension = null;
        StringBuilder messages = new StringBuilder();
        Map<String, String> metadataMap = new HashMap<>();

        try {
            Metadata metadata = new Metadata();
            fileType = detectFileType(file, metadata, metadataMap);
            fileExtension = getFileExtension(file.getOriginalFilename());
            logger.info("Detected file type: {}", fileType);

            model.addAttribute("fileType", fileType);
            model.addAttribute("fileExtension", fileExtension);
            model.addAttribute("fileSize", file.getSize());

            for (Map.Entry<String, String> entry : metadataMap.entrySet()) {
                logger.info("Metadata: {} = {}", entry.getKey(), entry.getValue());
            }

            String virusScanResult = scanWithClamAV(file, model);
            logger.info("Virus scan result: {}", virusScanResult);

            String clamavVersion = getClamAVVersion();
            model.addAttribute("clamavVersion", clamavVersion);

            boolean isFileTypeAllowed = isFileTypeAllowed(fileType);
            boolean isFileExtensionAllowed = isFileExtensionAllowed(fileExtension);

            if (!isFileTypeAllowed) {
                messages.append("File type not allowed: ").append(fileType).append("<br/>");
            }
            if (!isFileExtensionAllowed) {
                messages.append("File extension not allowed: ").append(fileExtension).append("<br/>");
            }

            if (!isFileTypeAllowed || !isFileExtensionAllowed) {
                model.addAttribute("danger", true);
            }

            if (virusScanResult.contains("FOUND")) {
                messages.append("Virus detected in file: ").append(virusScanResult);
                model.addAttribute("danger", true);
            }

            model.addAttribute("message", messages.toString());

            addWhitelistAttributes(model);

            if (model.containsAttribute("danger") && model.getAttribute("danger").equals(true)) {
                return "public/safe_upload";
            }

            String filePath = saveFile(file);
            String fileUrl = "/uploads/" + file.getOriginalFilename();

            model.addAttribute("filePath", fileUrl);
            model.addAttribute("virusScanResult", virusScanResult);
            model.addAttribute("message", "File uploaded successfully with checks: " + file.getOriginalFilename() + " (" + fileType + ") " + virusScanResult);
            model.addAttribute("danger", false);

            return "public/safe_upload";
        } catch (Exception e) {
            logger.error("Failed to upload file with checks", e);
            model.addAttribute("message", "Failed to upload file: " + e.getMessage());
            model.addAttribute("danger", true);
            addWhitelistAttributes(model);
            return "public/safe_upload";
        }
    }

    private void addWhitelistAttributes(Model model) {
        model.addAttribute("whitelistedFileTypes", String.join(", ", fileTypeWhitelistConfig.getWhitelistedFileTypes()));
        model.addAttribute("whitelistedExtensions", String.join(", ", fileTypeWhitelistConfig.getWhitelistedExtensions()));
        model.addAttribute("maxFileSizeMB", maxFileSize / 1048576);
    }

    private String saveFile(MultipartFile file) throws IOException {
        logger.info("Saving file: {}", file.getOriginalFilename());
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            logger.info("Creating upload directory: {}", uploadPath.toAbsolutePath().toString());
            Files.createDirectories(uploadPath);
        }
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        logger.info("File path: {}", filePath.toString());
        Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        logger.info("File saved at: {}", filePath.toAbsolutePath().toString());
        return filePath.toString();
    }

    private String detectFileType(MultipartFile file, Metadata metadata, Map<String, String> metadataMap) throws IOException {
        Tika tika = new Tika();
        try (InputStream stream = file.getInputStream()) {
            AutoDetectParser parser = new AutoDetectParser(TikaConfig.getDefaultConfig());
            ContentHandler handler = new BodyContentHandler();
            parser.parse(stream, handler, metadata, new ParseContext());
        } catch (Exception e) {
            logger.error("Error detecting file type and metadata", e);
        }

        Detector detector = new DefaultDetector();
        byte[] fileBytes = file.getBytes();
        try (InputStream stream = new ByteArrayInputStream(fileBytes)) {
            Metadata detectorMetadata = new Metadata();
            detector.detect(stream, detectorMetadata);
            for (String name : detectorMetadata.names()) {
                metadataMap.put(name, detectorMetadata.get(name));
            }
        } catch (Exception e) {
            logger.error("Error detecting with detector: " + detector.getClass().getName(), e);
        }

        for (String name : metadata.names()) {
            metadataMap.put(name, metadata.get(name));
        }

        try (InputStream stream = new ByteArrayInputStream(fileBytes)) {
            return tika.detect(stream, metadata);
        }
    }

    private boolean isFileTypeAllowed(String fileType) {
        Set<String> whitelistedFileTypes = fileTypeWhitelistConfig.getWhitelistedFileTypes();
        return whitelistedFileTypes.contains(fileType);
    }

    private boolean isFileExtensionAllowed(String fileExtension) {
        Set<String> whitelistedExtensions = fileTypeWhitelistConfig.getWhitelistedExtensions();
        return whitelistedExtensions.contains(fileExtension);
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    private String scanWithClamAV(MultipartFile file, Model model) {
        StringBuilder result = new StringBuilder();
        try (Socket socket = new Socket(clamavHost, clamavPort)) {
            InputStream inputStream = file.getInputStream();
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            int bytesRead;

            socket.getOutputStream().write("zINSTREAM\0".getBytes());

            while ((bytesRead = inputStream.read(buffer.array())) != -1) {
                socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(bytesRead).array());
                socket.getOutputStream().write(buffer.array(), 0, bytesRead);
            }
            socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(0).array());

            byte[] reply = new byte[2048];
            int responseBytes = socket.getInputStream().read(reply);
            String response = new String(reply, 0, responseBytes).trim();

            model.addAttribute("virusScanResult", response);
            result.append(response);

            return result.toString();
        } catch (IOException e) {
            logger.error("Failed to scan file with ClamAV", e);
            result.append("Failed to scan file with ClamAV: ").append(e.getMessage());
            throw new RuntimeException(result.toString(), e);
        }
    }

    private String getClamAVVersion() throws IOException {
        try (Socket socket = new Socket(clamavHost, clamavPort)) {
            socket.getOutputStream().write("VERSION\0".getBytes());
            byte[] buffer = new byte[2048];
            int bytesRead = socket.getInputStream().read(buffer);
            if (bytesRead != -1) {
                return new String(buffer, 0, bytesRead).trim();
            } else {
                throw new IOException("No response from ClamAV server");
            }
        }
    }
}