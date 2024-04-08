package com.jxx.vacation.core.vacation.domain.service;

import com.jxx.vacation.core.vacation.domain.entity.LeaveDeduct;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import com.jxx.vacation.core.vacation.domain.entity.VacationDuration;
import lombok.extern.slf4j.Slf4j;

import static com.jxx.vacation.core.vacation.domain.entity.VacationType.*;

@Slf4j
public class VacationCalculator {

    private final static float HALF_VACATION_DEDUCTED_VALE = 0.5F;

    public static float getVacationDuration(Vacation vacation) {
        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct()) && isMoreOneDay(vacation)) {
            return vacation.getVacationDuration().calculateDate();
        }

        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct()) && isHalfDay(vacation)) {
            return HALF_VACATION_DEDUCTED_VALE;
        }

        if (!LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct())) {
            log.warn("차감 연차가 아닙니다. 차감을 하지 않습니다.");
        }

        return 0F;
    }

    public static float getVacationDuration(VacationDuration vacationDuration, LeaveDeduct leaveDeduct) {
        // 하루 이상 연차
        if (!LeaveDeduct.NOT_DEDUCT.equals(leaveDeduct) && MORE_DAY.equals(vacationDuration.getVacationType())) {
            return vacationDuration.calculateDate();
        }

        // 반차
        if (!LeaveDeduct.NOT_DEDUCT.equals(leaveDeduct) && (HALF_VACATION_TYPE.contains(vacationDuration.getVacationType()))) {
            return HALF_VACATION_DEDUCTED_VALE;
        }

        return 0F;
    }

    public float getVacationDays(Vacation vacation) {
        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct()) && isMoreOneDay(vacation)) {
            return vacation.getVacationDuration().calculateDate();
        }

        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct()) && isHalfDay(vacation)) {
            return HALF_VACATION_DEDUCTED_VALE;
        }

        if (!LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct())) {
            log.warn("차감 연차가 아닙니다. 차감을 하지 않습니다.");
        }

        return 0F;
    }

    private static boolean isMoreOneDay(Vacation vacation) {
        return MORE_DAY.equals(vacation.vacationType());
    }

    private static boolean isHalfDay(Vacation vacation) {
        return HALF_MORNING.equals(vacation.vacationType()) || HALF_AFTERNOON.equals(vacation.vacationType());
    }
}
