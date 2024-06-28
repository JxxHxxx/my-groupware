package com.jxx.vacation.api.messaging.dto.request;

public record MessagePagingSearchCond(
        int page,
        int size,
        String startDate,
        String endDate
) {
}
