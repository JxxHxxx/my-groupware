package com.jxx.groupware.api.file.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    @Value("${file.store.root.work-ticket}")
    private String STORE_ROOT_DIR;
    @Value("${file.download.root}")
    private String DOWNLOAD_ROOT_DIR;

    @Override
    @Transactional
    public String store(MultipartFile file) throws IOException {
        final String originalFilename = file.getOriginalFilename(); // 클라이언트로부터 업로드된 파일의 본래명

        final String extension = FilenameUtils.getExtension(originalFilename); // 확장자

        String tmpFileName = originalFilename;
        // 파일 명이 존재할 때 까지 루프
        int fileNameUpdateRetryCount = 1;
        while (Files.exists(Path.of(STORE_ROOT_DIR + tmpFileName))) {
            log.info("{} is already exist, add suffix _copy, retryCount : {}", STORE_ROOT_DIR + tmpFileName, fileNameUpdateRetryCount);
            tmpFileName = FilenameUtils.getBaseName(tmpFileName) + "_copy." + extension;
            fileNameUpdateRetryCount++;
        }

        final String absoluteStorePath = STORE_ROOT_DIR + tmpFileName;
        log.info("to be saved file path : {}", absoluteStorePath);
        file.transferTo(Path.of(absoluteStorePath));

        String url = Base64.getEncoder().encodeToString((absoluteStorePath).getBytes(StandardCharsets.UTF_8));

        return url;
    }

    @Override
    public Path load(String encodeUrl) throws IOException {
        String filename = new String(Base64.getDecoder().decode(encodeUrl));
        String baseName = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);
        final String savedFilename = baseName + "." + extension;
       return Files.copy(Path.of(filename), Path.of(DOWNLOAD_ROOT_DIR + savedFilename));
    }

    @Override
    public void delete() {

    }
}
