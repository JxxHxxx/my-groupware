package com.jxx.groupware.api.work.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WorkTicketAttachmentRequest {
    private final String workTicketId;
}
