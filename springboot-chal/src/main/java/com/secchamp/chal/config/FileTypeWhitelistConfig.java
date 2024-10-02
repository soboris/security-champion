package com.secchamp.chal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.HashSet;
import java.util.Set;

@Component
public class FileTypeWhitelistConfig {

    @Value("${whitelist.filetypes}")
    private String[] whitelistedFileTypesArray;

    @Value("${whitelist.extensions}")
    private String[] whitelistedExtensionsArray;

    private Set<String> whitelistedFileTypes;
    private Set<String> whitelistedExtensions;

    @PostConstruct
    public void init() {
        whitelistedFileTypes = new HashSet<>();
        for (String fileType : whitelistedFileTypesArray) {
            whitelistedFileTypes.add(fileType.trim());
        }

        whitelistedExtensions = new HashSet<>();
        for (String extension : whitelistedExtensionsArray) {
            whitelistedExtensions.add(extension.trim());
        }
    }

    public Set<String> getWhitelistedFileTypes() {
        return whitelistedFileTypes;
    }

    public Set<String> getWhitelistedExtensions() {
        return whitelistedExtensions;
    }
}