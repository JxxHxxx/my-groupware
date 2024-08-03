package com.jxx.groupware.api.vacation.dto.request;

public record VacationTypePolicyForm(
        Float vacationDay,
        String vacationType,
        String vacationTypeName
        ) {
}
