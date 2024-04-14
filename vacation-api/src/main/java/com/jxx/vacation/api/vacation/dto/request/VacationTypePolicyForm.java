package com.jxx.vacation.api.vacation.dto.request;

public record VacationTypePolicyForm(
        String companyId,
        Float vacationDay,
        String vacationType
        ) {
}
