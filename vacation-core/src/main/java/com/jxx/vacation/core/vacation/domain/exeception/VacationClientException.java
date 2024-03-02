package com.jxx.vacation.core.vacation.domain.exeception;


import lombok.Getter;

@Getter
public class VacationClientException extends RuntimeException {

    private final String clientId;

    public VacationClientException(String message, String clientId) {
        super(message);
        this.clientId = clientId;
    }
}
