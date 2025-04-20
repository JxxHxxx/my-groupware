package com.jxx.groupware.api.file.domain;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Base64;

@Slf4j
@Service
public class FileResolver {

    public Path download(String encodeUrl) {
        String filename = new String(Base64.getDecoder().decode(encodeUrl));
        return Path.of(filename);
    }
}
