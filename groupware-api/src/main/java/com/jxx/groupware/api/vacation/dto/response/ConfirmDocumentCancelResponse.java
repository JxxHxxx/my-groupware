package com.jxx.groupware.api.vacation.dto.response;

import com.jxx.groupware.core.message.body.vendor.confirm.ConfirmStatus;

public record ConfirmDocumentCancelResponse(
        String confirmDocumentId,
        String requesterId,
        ConfirmStatus confirmStatus
) {
}
