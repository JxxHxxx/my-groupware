package com.jxx.groupware.api.member.presentation;

/** 인증 세션 관련 예외 **/
public class UnAuthenticationException extends RuntimeException {
    private String message;

    public UnAuthenticationException() {
    }

    public UnAuthenticationException(String message) {
        super(message);
        this.message = message;
    }

}
