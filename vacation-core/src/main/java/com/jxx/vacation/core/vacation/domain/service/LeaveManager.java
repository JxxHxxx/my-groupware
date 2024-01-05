package com.jxx.vacation.core.vacation.domain.service;

import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeaveManager {

    private final static float HALF_VACATION_DEDUCTED_VALE = 0.5F;

    public static float calculateDeductionDateBecauseOf(Vacation vacation) {
        if (vacation.isDeductedFromLeave() && isMoreOneDay(vacation)) {
            return vacation.getVacationDuration().calculateDate();
        }

        if (vacation.isDeductedFromLeave() && isHalfDay(vacation)) {
            return HALF_VACATION_DEDUCTED_VALE;
        }

        if (!vacation.isDeductedFromLeave() || isNotDeducted(vacation)) {
            log.warn("차감 연차가 아닙니다. 차감을 하지 않습니다.");
        }

        return 0F;
    }

    private static boolean isNotDeducted(Vacation vacation) {
        return VacationType.NOT_DEDUCTED.equals(vacation.vacationType());
    }

    private static boolean isMoreOneDay(Vacation vacation) {
        return VacationType.MORE_DAY.equals(vacation.vacationType());
    }

    private static boolean isHalfDay(Vacation vacation) {
        return VacationType.HALF_MORNING.equals(vacation.vacationType()) || VacationType.HALF_AFTERNOON.equals(vacation.vacationType());
    }
}
