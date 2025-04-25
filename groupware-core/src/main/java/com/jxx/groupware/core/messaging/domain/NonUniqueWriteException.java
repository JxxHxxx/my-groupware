package com.jxx.groupware.core.messaging.domain;


public class NonUniqueWriteException extends RuntimeException {

    public NonUniqueWriteException(String message){
        super(message);
    }
}
