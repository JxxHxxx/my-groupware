package com.jxx.groupware.core.vacation.domain.dto;


import java.time.LocalDateTime;

public record UpdateVacationDurationForm(
        Long vacationDurationId, // 신규로 추가하면 null 을 넘겨야함
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
