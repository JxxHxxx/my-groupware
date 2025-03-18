package com.jxx.groupware.core.messaging.domain;

public class MessageClientException extends RuntimeException {

    private String errCode;
    private String message;

    public MessageClientException() {
    }

    public MessageClientException(String errCode, String message) {
        this.errCode = errCode;
        this.message = message;

    }

    public MessageClientException(Throwable cause) {
        super(cause);
    }
}

