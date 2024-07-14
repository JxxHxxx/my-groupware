package com.jxx.groupware.core.vacation.domain.exeception;


/**
 * MemberLeave API - 비즈니스 로직 상 발생하는 예외입니다.
 * 해당 예외는 400 으로 예외 처리합니다.
 */

public class MemberLeaveException extends Exception {
    public MemberLeaveException(String message) {
        super(message);
    }
}
