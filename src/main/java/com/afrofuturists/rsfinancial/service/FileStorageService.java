package com.afrofuturists.rsfinancial.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Writes uploaded files to local disk under app.file-storage.upload-dir.
 * This is deliberately the ONLY class that knows files live on disk -
 * ClaimService just calls store()/loadAsUrl() and gets back a path/URL,
 * with no filesystem code of its own. The problem statement's "the more
 * that passes straight through automatically" principle points at
 * eventually replacing this with real cloud storage (S3, etc.) - when
 * that happens, only this class changes.
 */
@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.file-storage.upload-dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create upload directory: " + uploadRoot, e);
        }
    }

    /**
     * Stores the file under a subfolder (e.g. "claims/{claimId}") and
     * returns the path relative to uploadRoot - what gets saved as
     * ClaimDocument.storedPath. A random prefix on the filename avoids
     * collisions between two uploads with the same original name.
     */
    public String store(MultipartFile file, String subFolder) {
        String safeName = UUID.randomUUID() + "-" + sanitize(file.getOriginalFilename());
        Path targetDir = uploadRoot.resolve(subFolder).normalize();

        // Guard against a subFolder or filename that tries to escape
        // uploadRoot via "../" - sanitize() strips path separators from
        // the filename, this checks the resolved directory itself.
        if (!targetDir.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid storage path");
        }

        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(safeName);
            file.transferTo(targetFile);
            return uploadRoot.relativize(targetFile).toString().replace('\\', '/');
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file", e);
        }
    }

    /**
     * The public URL a stored path is served at - matches the
     * ResourceHandler mapping registered in WebMvcConfig.
     */
    public String toServedUrl(String storedPath) {
        return "/uploads/" + storedPath;
    }

    private String sanitize(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "file";
        }
        return originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
