package com.jxx.vacation.core.vacation.domain.exeception;

import lombok.Getter;

@Getter
public class AuthClientException extends RuntimeException {

    private String requesterId;

    public AuthClientException(String message, String requesterId) {
        super(message);
        this.requesterId = requesterId;
    }

    public AuthClientException(String message) {
        super(message);
    }
}
