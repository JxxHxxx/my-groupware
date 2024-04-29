package com.jxx.vacation.core.message.body.vendor.confirm;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VacationDurationModel {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public VacationDurationModel(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public VacationDurationModel() {
    }
}
