package com.jxx.vacation.core.vacation.domain;

import java.time.LocalDateTime;

public record RequestVacationDuration(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
