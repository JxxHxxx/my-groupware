package com.jxx.groupware.core.vacation.domain.exeception;

import lombok.Getter;

/** 인가 관련 예외 처리(해당 예외는 403 상태코드를 응답해야 합니다) **/

@Getter
public class AuthorizationException extends RuntimeException {
    private String requesterId;

    public AuthorizationException(String message, String requesterId) {
        super(message);
        this.requesterId = requesterId;
    }

    public AuthorizationException(String message) {
        super(message);
    }

}
