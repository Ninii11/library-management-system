
package com.example.library.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDirectory;

    public FileStorageService(
            @Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir)
                .toAbsolutePath().normalize();
    }

    public String saveFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "File is empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "File exceeds 5 MB");
        }

        String extension;

        try {
            byte[] header = file.getInputStream().readNBytes(12);

            if (header.length >= 3
                    && (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF) {
                extension = ".jpg";
            } else if (header.length >= 8
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 0x50
                    && header[2] == 0x4E
                    && header[3] == 0x47
                    && header[4] == 0x0D
                    && header[5] == 0x0A
                    && header[6] == 0x1A
                    && header[7] == 0x0A) {
                extension = ".png";
            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Only JPEG and PNG images are allowed");
            }

            Files.createDirectories(uploadDirectory);

            String filename = UUID.randomUUID() + extension;
            Path destination = uploadDirectory.resolve(filename);

            Files.copy(file.getInputStream(), destination);

            return destination.toString();

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not save file", e);
        }
    }

    public void deleteFile(String path) {
        if (path == null) return;

        try {
            Path file = Paths.get(path).toAbsolutePath().normalize();

            if (file.startsWith(uploadDirectory)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not delete file", e);
        }
    }
}