package com.secchamp.chal.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class HelpController {

    @GetMapping("/pages/help")
    public String directoryTraversal(@RequestParam(value = "filePath", required = false) String filePath, Model model) {
        if (filePath != null && !filePath.isEmpty()) {
            try {
                String content = readFileContent(filePath);
                model.addAttribute("fileContent", content);
            } catch (IOException e) {
                model.addAttribute("fileContent", "Error reading file: " + e.getMessage());
            }
        } else {
            model.addAttribute("fileContent", "No file specified.");
        }
        model.addAttribute("filePath", filePath);
        return "help";
    }

    private String readFileContent(String filePath) throws IOException {
        // Read the content of the specified path, assuming a relative path from the current directory
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
    }
}
