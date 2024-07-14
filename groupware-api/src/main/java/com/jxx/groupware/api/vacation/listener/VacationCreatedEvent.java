package com.jxx.groupware.api.vacation.listener;

import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.domain.entity.Vacation;

public record VacationCreatedEvent(
        MemberLeave memberLeave,
        Vacation vacation,
        float vacationDate,
        String title,
        String delegatorId,
        String delegatorName,
        String reason
) {
}
