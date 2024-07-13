package com.jxx.vacation.api.messaging.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

@Getter
public class MessagePagingSearchCond {
    private final int page;
    private final int size;
    private final String startDate;
    private final String endDate;



    public MessagePagingSearchCond(@PositiveOrZero int page, @PositiveOrZero int size, String startDate, String endDate) {
        this.page = page;
        this.size = size;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
