package com.jxx.groupware.api.file.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {

    /**
     * @return 파일이 저장된 위치
     **/
    UploadFile store(MultipartFile file) throws IOException;

    /**
     *
     */

    Path load(String encodeUrl) throws IOException;

    void delete();
}
