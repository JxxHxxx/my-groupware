package com.jxx.vacation.api.common.web;

public class ServerServiceException extends RuntimeException{

    private final RequestUri requestUri;

    public ServerServiceException(String message, RequestUri requestUri, Throwable cause) {
        super(message, cause);
        this.requestUri = requestUri;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }
}
