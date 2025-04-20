package com.jxx.groupware.api.file.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UUIDStorageService implements StorageService {

    @Value("${file.store.root}")
    private String FILE_STORE_ROOT;

    @Override
    @Transactional
    public UploadFile store(MultipartFile file) throws IOException {
        String uploadFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(uploadFilename);
        String stringUUID = UUID.randomUUID().toString();

        String storeFilename = stringUUID + "." + extension;

        String filePath = FILE_STORE_ROOT + storeFilename;
        file.transferTo(Path.of(filePath));
        log.info("uploadFile : {} storeFullPath: {}", uploadFilename, filePath);
        return new UploadFile(uploadFilename, storeFilename);
    }

    @Override
    public Path load(String uuid) throws IOException {
        return Path.of(uuid);
    }

    @Override
    public void delete() {

    }
}
