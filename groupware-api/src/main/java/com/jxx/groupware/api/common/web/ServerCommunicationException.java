package com.jxx.groupware.api.common.web;

import lombok.Getter;

@Getter
public class ServerCommunicationException extends RuntimeException{

    private static final Integer NOT_HTTP_STATUS_CODE_VALUE = 999;
    private final RequestUri requestUri;
    private final Integer statusCode; // HTTP 상태 코드
    private String errorCode; // 타 서버에서 내려주는 오류 코드
    private final String message;  // 오류 메시지
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

    public ServerCommunicationException(Integer statusCode, String errorCode, String message, RequestUri requestUri, Throwable cause) {
        super(message, cause);
        this.requestUri = requestUri;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
