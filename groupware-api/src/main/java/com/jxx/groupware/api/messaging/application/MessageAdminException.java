package com.jxx.groupware.api.messaging.application;


import com.jxx.groupware.api.common.exception.BaseException;
import com.jxx.groupware.api.common.exception.ErrorCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class MessageAdminException extends BaseException {

    private final int httpStatus;
    public MessageAdminException(ErrorCode errorCode) {
        super(errorCode);
        this.httpStatus = errorCode.getStatusCode();
    }
}
