package com.jxx.vacation.core.vacation.domain.dto;


import java.time.LocalDateTime;

public record UpdateVacationDurationForm(
        Long vacationDurationId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
