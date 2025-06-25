package com.example.vetclinic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads/"; // будет рядом с jar/проэктом

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString() + "." + ext;
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filepath = uploadPath.resolve(filename);
            Files.write(filepath, file.getBytes());

            // Возвращаем URL для доступа к файлу
            String baseUrl = "http://localhost:8080"; // ваш адрес backend
            String fileUrl = baseUrl + "/uploads/" + filename;
            return ResponseEntity.ok(new UploadResponse(fileUrl));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed");
        }
    }

    // Вложенный класс для возвращаемого JSON с URL
    public static class UploadResponse {
        public String url;
        public UploadResponse(String url) { this.url = url; }
    }
}
