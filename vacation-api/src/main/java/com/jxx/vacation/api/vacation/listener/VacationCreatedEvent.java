package com.jxx.vacation.api.vacation.listener;

import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;

public record VacationCreatedEvent(
        MemberLeave memberLeave,
        Vacation vacation,
        float vacationDate,
        String title,
        String delegatorName,
        String reason
) {
}
