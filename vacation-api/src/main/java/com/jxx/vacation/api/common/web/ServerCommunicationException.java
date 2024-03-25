package com.jxx.vacation.api.common.web;

import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import lombok.Getter;

@Getter
public class ServerCommunicationException extends RuntimeException{

    private static final Integer NOT_HTTP_STATUS_CODE_VALUE = -999;
    private final RequestUri requestUri;
    private final Integer statusCode;
    private final String message;
    public ServerCommunicationException(Integer statusCode, String message, RequestUri requestUri, Throwable cause) {
        super(message, cause);
        this.requestUri = requestUri;
        this.statusCode = statusCode;
        this.message = message;
    }

    public ServerCommunicationException(String message, RequestUri requestUri, Throwable cause) {
        super(message, cause);
        this.requestUri = requestUri;
        this.message = message;
        this.statusCode = NOT_HTTP_STATUS_CODE_VALUE;
    }
}
