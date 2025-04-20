package com.jxx.groupware.api.file.presentation;

import com.jxx.groupware.api.file.domain.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
public class FileApiController {

    private final StorageService storageService;

    public FileApiController(@Qualifier("UUIDStorageService") StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/api/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        storageService.store(file);
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/api/download")
    public ResponseEntity<?> download(@RequestParam("path") String encodeUrl) throws IOException {
        Path load = storageService.load(encodeUrl);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + load.getFileName() + "\"").body(load.toFile());
    }

}
