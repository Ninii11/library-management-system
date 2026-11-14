
package com.example.library.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final Path uploadDirectory;

    public FileStorageService(
            @Value("${app.upload.dir:uploads}") String uploadDir) {

        this.uploadDirectory = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    public String saveFile(MultipartFile file) {

        // 1. Check if file is empty
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is empty");
        }

        // 2. Check file size (maximum 5 MB)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File exceeds 5 MB");
        }

        try (InputStream inputStream = file.getInputStream()) {

            // 3. Read the file header
            byte[] header = inputStream.readNBytes(12);

            String extension;

            // 4. Validate JPEG signature
            if (header.length >= 3
                    && (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF) {

                extension = ".jpg";

                // 5. Validate PNG signature
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

            // 6. Create uploads folder if necessary
            Files.createDirectories(uploadDirectory);

            // 7. Generate unique filename
            String filename = UUID.randomUUID() + extension;

            Path destination = uploadDirectory.resolve(filename);

            // 8. Save file and close its input stream automatically
            try (InputStream copyStream = file.getInputStream()) {
                Files.copy(copyStream, destination);
            }

            // 9. Return saved file path
            return destination.toString();

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not save file",
                    e);
        }
    }

    public void deleteFile(String path) {

        if (path == null) {
            return;
        }

        try {
            Path file = Paths.get(path)
                    .toAbsolutePath()
                    .normalize();

            // Only delete files inside uploads folder
            if (file.startsWith(uploadDirectory)
                    && !file.equals(uploadDirectory)) {

                Files.deleteIfExists(file);
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not delete file",
                    e);
        }
    }
}
