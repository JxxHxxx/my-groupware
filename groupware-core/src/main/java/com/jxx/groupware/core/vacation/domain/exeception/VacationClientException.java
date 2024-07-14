package com.jxx.groupware.core.vacation.domain.exeception;


import lombok.Getter;

@Getter
public class VacationClientException extends RuntimeException {

    private String clientId;

    public VacationClientException(String message, String clientId) {
        super(message);
        this.clientId = clientId;
    }

    public VacationClientException(String message) {
        super(message);

    }
}
