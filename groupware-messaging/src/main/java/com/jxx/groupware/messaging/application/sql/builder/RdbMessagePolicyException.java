package com.jxx.groupware.messaging.application.sql.builder;

import lombok.Getter;

@Getter
public class RdbMessagePolicyException extends RuntimeException {
    private final String message;
    public RdbMessagePolicyException(String message) {
        super(message);
        this.message = message;
    }
}
