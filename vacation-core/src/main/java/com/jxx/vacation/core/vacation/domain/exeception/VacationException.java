package com.jxx.vacation.core.vacation.domain.exeception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VacationException extends RuntimeException {
    public VacationException(String message) {
        super(message);
    }

}
