package com.jxx.groupware.core.work.domain.exception;

import lombok.Getter;

@Getter
public class WorkClientException extends RuntimeException {
    private String clientId;
    private String message;

    public WorkClientException(String message) {
        super(message);
        this.message = message;
    }

    public WorkClientException(String clientId, String message) {
        super(message);
        this.clientId = clientId;
        this.message = message;
    }

    @Override
    public String toString() {
        return "WorkClientException{" +
                "clientId='" + clientId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
