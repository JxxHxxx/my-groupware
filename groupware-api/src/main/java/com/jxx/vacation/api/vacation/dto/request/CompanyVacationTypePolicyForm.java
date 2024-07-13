package com.jxx.vacation.api.vacation.dto.request;

public record CompanyVacationTypePolicyForm(
        String companyId,
        String vacationType,
        Float vacationDay
) {
}
