package com.jxx.vacation.core.message.body.vendor.confirm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class VacationDurationModel {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
}
