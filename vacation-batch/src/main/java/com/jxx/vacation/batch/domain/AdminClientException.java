package com.jxx.vacation.batch.domain;

import lombok.Getter;

@Getter
public class AdminClientException extends RuntimeException {

    private String errCode;
    public AdminClientException() {
    }

    public AdminClientException(String message) {
        super(message);
    }

    public AdminClientException(String message, String errCode) {
        super(message);
        this.errCode = errCode;
    }
}
