package com.jxx.vacation.core.message.body.vendor.confirm;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
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
