package com.jxx.vacation.api.vacation.dto.response;

public record ConfirmDocumentResponse(
        Long pk,
        String confirmDocumentId,
        String companyId,
        String departmentId,
        String createSystem,
        String confirmStatus,
        String documentType,
        String requesterId
) {
}
