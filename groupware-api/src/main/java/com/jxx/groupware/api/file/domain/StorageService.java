package com.jxx.groupware.api.file.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {

    void init();
    void store(MultipartFile file) throws IOException;
    Path load(String filename);
    void delete();
}
