package com.jxx.vacation.api.vacation.dto.response;

public record ConfirmDocumentRaiseResponse(
        Long confirmDocumentPk,
        String requesterId,
        String confirmStatus
) {
}
