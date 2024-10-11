package com.jxx.groupware.api.vacation.listener;

import com.jxx.groupware.core.vacation.domain.entity.Vacation;

public record CommonVacationCreateEvent(
        Vacation vacation,
        Float vacationDate,
        String departmentId,
        String departmentName,
        String requesterName
) {
}
