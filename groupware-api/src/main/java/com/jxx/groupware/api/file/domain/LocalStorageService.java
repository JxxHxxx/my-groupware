package com.jxx.groupware.api.file.domain;

import com.jxx.groupware.core.work.domain.WorkTicketAttachment;
import com.jxx.groupware.core.work.infra.WorkTicketAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
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

    // TODO 추후 yml 설정 값으로 이동
    private static final String STORE_ROOT_DIR = "D:\\file\\";

    private final WorkTicketAttachmentRepository workTicketAttachmentRepository;

    @Override
    public void init() {

    }

    @Override
    @Transactional
    public void store(MultipartFile file) throws IOException {
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
        WorkTicketAttachment workTicketAttachment = WorkTicketAttachment.builder()
                .attachmentUrl(url)
                .workTicket(null)
                .build();

        workTicketAttachmentRepository.save(workTicketAttachment);
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public void delete() {

    }
}
