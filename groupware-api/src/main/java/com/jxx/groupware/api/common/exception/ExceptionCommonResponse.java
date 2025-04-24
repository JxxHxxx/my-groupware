package com.jxx.groupware.api.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionCommonResponse {
    private final String errorCode;
    private final String errorMessage;
}
