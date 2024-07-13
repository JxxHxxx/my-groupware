package com.jxx.vacation.api.vacation.dto.response;

public record ClientExceptionResponse(
        int status,
        String clientId,
        String message
) {
}