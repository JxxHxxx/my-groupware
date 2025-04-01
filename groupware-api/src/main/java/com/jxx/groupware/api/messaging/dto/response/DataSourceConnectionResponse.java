package com.jxx.groupware.api.messaging.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class DataSourceConnectionResponse {
    private final boolean isConnectionActive;
    private final String message;
    private final LocalDateTime responseTime;
}
