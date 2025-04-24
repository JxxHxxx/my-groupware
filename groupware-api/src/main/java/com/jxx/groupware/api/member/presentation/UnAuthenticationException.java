package com.jxx.groupware.api.member.presentation;

import com.jxx.groupware.api.common.exception.BaseException;
import com.jxx.groupware.api.common.exception.ErrorCode;

/** 인증 세션 관련 예외(해당 예외는 401 상태코드를 응답해야 합니다) **/
public class UnAuthenticationException extends BaseException {
    public UnAuthenticationException() {
        super(ErrorCode.COM_AUTH_F_001);
    }
}
