package com.jxx.groupware.api.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExceptionCommonResponse {
    private final String code;
    private final String applicationDomain;
    private final String errMsg;
}
