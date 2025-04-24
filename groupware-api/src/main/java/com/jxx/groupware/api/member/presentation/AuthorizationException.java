package com.jxx.groupware.api.member.presentation;


import com.jxx.groupware.api.common.exception.BaseException;
import com.jxx.groupware.api.common.exception.ErrorCode;

/** 인가 관련 예외 처리(해당 예외는 403 상태코드를 응답해야 합니다) **/

public class AuthorizationException extends BaseException {

    public AuthorizationException() {
        super(ErrorCode.COM_AUTH_F_002);
    }

}
