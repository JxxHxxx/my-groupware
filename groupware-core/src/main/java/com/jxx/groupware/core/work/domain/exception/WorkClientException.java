package com.jxx.groupware.core.work.domain.exception;

import lombok.Getter;

@Getter
public class WorkClientException extends RuntimeException {
    private String message;

    public WorkClientException(String message) {
        super(message);
        this.message = message;
    }
}
