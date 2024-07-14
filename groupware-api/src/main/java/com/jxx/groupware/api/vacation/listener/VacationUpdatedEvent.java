package com.jxx.groupware.api.vacation.listener;

import com.jxx.groupware.core.vacation.domain.entity.Vacation;

public record VacationUpdatedEvent(
        String delegatorId,
        String delegatorName,
        String reason,
        Vacation vacation,
        Long contentPk
) {
}