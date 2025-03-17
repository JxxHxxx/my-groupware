package com.jxx.groupware.core.messaging.body.vendor.confirm;

import lombok.Getter;

@Getter
public class VacationDurationModel {
    private String startDateTime;
    private String endDateTime;


    public VacationDurationModel(String startDateTime, String endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public VacationDurationModel() {
    }
}
