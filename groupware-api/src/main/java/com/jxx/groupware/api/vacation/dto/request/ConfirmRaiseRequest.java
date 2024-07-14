package com.jxx.groupware.api.vacation.dto.request;

public record ConfirmRaiseRequest(
        String companyId,
        String departmentId,
        String requesterId
) {
}
