package com.jxx.vacation.api.vacation.dto.request;

public record ConfirmRaiseRequest(
        String companyId,
        String departmentId,
        String requesterId
) {
}
