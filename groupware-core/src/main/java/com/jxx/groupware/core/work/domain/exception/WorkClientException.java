package com.jxx.groupware.core.work.domain.exception;

import com.jxx.groupware.core.work.WorkResponseCode;
import lombok.Getter;

@Getter
public class WorkClientException extends RuntimeException {
    private String errCode;
    private String message;

    public WorkClientException(String message) {
        super(message);
        this.message = message;
    }

    public WorkClientException(WorkResponseCode workResponseCode) {
        super(workResponseCode.getDescription());
        this.errCode = workResponseCode.getCode();
        this.message = workResponseCode.getDescription();
    }

    @Override
    public String toString() {
        return "WorkClientException{" +
                "clientId='" + errCode + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
