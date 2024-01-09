package com.jxx.vacation.core.vacation.domain.service;

import com.jxx.vacation.core.vacation.domain.entity.Vacation;

public class VacationPolicy {

    public boolean deductionVacation(Vacation vacation) {
        return vacation.validateDeductedLeave();
    }
}
