package com.jxx.groupware.core.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HttpApiResponse<T> {
    private final int statusCode;
    private final T data;
}
