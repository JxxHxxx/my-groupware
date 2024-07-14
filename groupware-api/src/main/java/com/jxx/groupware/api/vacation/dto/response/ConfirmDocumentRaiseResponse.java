package com.jxx.groupware.api.vacation.dto.response;

public record ConfirmDocumentRaiseResponse(
        String confirmDocumentId,
        String requesterId,
        String confirmStatus
) {
}
