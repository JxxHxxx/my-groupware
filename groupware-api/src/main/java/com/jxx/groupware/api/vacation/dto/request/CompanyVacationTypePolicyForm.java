package com.jxx.groupware.api.vacation.dto.request;

public record CompanyVacationTypePolicyForm(
        String companyId,
        String vacationType,
        Float vacationDay
) {
}
