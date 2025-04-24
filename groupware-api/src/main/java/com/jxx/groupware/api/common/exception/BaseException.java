package com.jxx.groupware.api.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 예외 클래스 공통 틀
 */

@ToString
@Getter
@NoArgsConstructor
public abstract class BaseException extends RuntimeException {
    private String errorCode;
    private String errorMessage;

    public BaseException(ErrorCode responseCode) {
        super(responseCode.getErrorMessage());
        this.errorCode = responseCode.getErrorCode();
        this.errorMessage = responseCode.getErrorMessage();
    }

    public ExceptionCommonResponse toExceptionCommonResponse() {
        return new ExceptionCommonResponse(errorCode, errorMessage);
    }
}