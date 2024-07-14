package com.jxx.groupware.api.vacation.dto.response;

import java.time.LocalDateTime;

public record ApprovalServiceResponse(
        Long messagePk,
        LocalDateTime eventTime
) {
}
