package com.jxx.vacation.api.vacation.dto;

import java.time.LocalDateTime;

public record ApprovalServiceResponse(
        Long messagePk,
        LocalDateTime eventTime
) {
}
