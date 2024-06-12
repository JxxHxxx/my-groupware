package com.jxx.vacation.core.vacation.domain.dto;

import java.time.LocalDateTime;

public record VacationDurationDto(
        Long vacationDurationId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Float useLeaveValue
) {
}
