package com.jxx.vacation.api.vacation.listener;

public record CommonVacationCreateEvent(
        String requesterId,
        String companyId,
        String departmentId,
        Float vacationDate,
        Long vacationId
) {
}
