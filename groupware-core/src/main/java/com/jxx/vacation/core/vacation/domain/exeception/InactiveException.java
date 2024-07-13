package com.jxx.vacation.core.vacation.domain.exeception;

/**
 * 사용자 및 사용자의 조직 상태가 비활성화로 인해 특정 로직을 실행할 수 없는 경우 사용
 */
public class InactiveException extends Exception{

    public InactiveException(String message) {
        super(message);
    }
}
