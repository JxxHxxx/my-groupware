package com.jxx.groupware.core.messaging.domain;

/** 메시지 처리가 불가능한 경우 이 예외를 던집니다. **/
public class UnProcessableException extends RuntimeException {

    public UnProcessableException(String message){
        super(message);
    }
}
