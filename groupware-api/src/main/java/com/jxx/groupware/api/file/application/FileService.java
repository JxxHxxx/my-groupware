package com.jxx.groupware.api.file.application;

import com.jxx.groupware.api.file.domain.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final StorageService storageService;
    public void upload(MultipartFile file) throws IOException {
        log.info("file name : {}", file.getResource().getFilename());
        log.info("파일 업로드 처리");
        storageService.store(file);
    };
}
