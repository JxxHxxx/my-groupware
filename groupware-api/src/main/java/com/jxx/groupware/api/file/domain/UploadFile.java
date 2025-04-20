package com.jxx.groupware.api.file.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UploadFile {
    private final String uploadFilename;
    private final String storeFilename;
}
