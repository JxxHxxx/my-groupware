package com.jxx.groupware.api.vacation.listener;

public record CommonVacationCreateEvent(
        String requesterId,
        String companyId,
        String departmentId,
        Float vacationDate,
        Long vacationId
) {
}
