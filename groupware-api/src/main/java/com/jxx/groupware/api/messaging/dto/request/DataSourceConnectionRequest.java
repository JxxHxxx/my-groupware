package com.jxx.groupware.api.messaging.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DataSourceConnectionRequest {
    private final String driverClassName;
    private final String jdbcUrl;
    private final String userName;
    private final String password;
}
