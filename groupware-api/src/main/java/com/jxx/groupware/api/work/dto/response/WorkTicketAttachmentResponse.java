package com.jxx.groupware.api.work.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WorkTicketAttachmentResponse {
    private final String uploadFilename;
    // UUID
    private final String storeFilename;
}
