package com.jxx.vacation.api.vacation.dto;

import com.jxx.vacation.core.domain.MessageStatus;

public record ApprovalServiceResponse(
        Long messagePk,
        MessageStatus messageStatus,
        String messageDescription

) {
}
