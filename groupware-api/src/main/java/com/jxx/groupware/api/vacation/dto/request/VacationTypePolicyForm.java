package com.jxx.groupware.api.vacation.dto.request;

public record VacationTypePolicyForm(
        String companyId,
        Float vacationDay,
        String vacationType
        ) {
}
