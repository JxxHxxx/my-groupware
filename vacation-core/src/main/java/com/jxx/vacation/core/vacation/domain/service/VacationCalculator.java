package com.jxx.vacation.core.vacation.domain.service;

import com.jxx.vacation.core.vacation.domain.entity.LeaveDeduct;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import com.jxx.vacation.core.vacation.domain.entity.VacationDuration;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.jxx.vacation.core.vacation.domain.entity.VacationType.*;

@Slf4j
public class VacationCalculator {

    private final static float HALF_VACATION_DEDUCTED_VALE = 0.5F;
    private static long DATE_ADJUSTMENTS_VALUE = 1l;


    public static Float getVacationDuration(Vacation vacation) {
        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct()) && isMoreOneDay(vacation)) {
            return 1F;
        }

        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct()) && isHalfDay(vacation)) {
            return HALF_VACATION_DEDUCTED_VALE;
        }

        if (!LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct())) {
            log.warn("차감 연차가 아닙니다. 차감을 하지 않습니다.");
        }

        return 0F;
    }

    public static float getVacationDuration(VacationType vacationType, VacationDuration vacationDuration, LeaveDeduct leaveDeduct) {
        // 하루 이상 연차
        if (!LeaveDeduct.NOT_DEDUCT.equals(leaveDeduct) && MORE_DAY.equals(vacationType)) {
            return vacationDuration.calculateDate();
        }

        // 반차
        if (!LeaveDeduct.NOT_DEDUCT.equals(leaveDeduct) && (HALF_VACATION_TYPE.contains(vacationType))) {
            return HALF_VACATION_DEDUCTED_VALE;
        }

        return 0F;
    }

//    public float getVacationDays(Vacation vacation) {
//        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct()) && isMoreOneDay(vacation)) {
//            return vacation.getVacationDuration().calculateDate();
//        }
//
//        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct()) && isHalfDay(vacation)) {
//            return HALF_VACATION_DEDUCTED_VALE;
//        }
//
//        if (!LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct())) {
//            log.warn("차감 연차가 아닙니다. 차감을 하지 않습니다.");
//        }
//
//        return 0F;
//    }

    private static boolean isMoreOneDay(Vacation vacation) {
        return MORE_DAY.equals(vacation.vacationType());
    }

    private static boolean isHalfDay(Vacation vacation) {
        return HALF_MORNING.equals(vacation.vacationType()) || HALF_AFTERNOON.equals(vacation.vacationType());
    }

    public static float calculateUseLeaveValue(LeaveDeduct leaveDeduct, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long vacationDateCount = 0;
        long notWorkingDateCount = 0;
        if (LeaveDeduct.isLeaveDeductVacation(leaveDeduct)) {
            vacationDateCount = ChronoUnit.DAYS.between(startDateTime, endDateTime) + DATE_ADJUSTMENTS_VALUE;
            notWorkingDateCount = countNotWorkingDate(startDateTime, endDateTime);
        }

        if (vacationDateCount - notWorkingDateCount < 0) {
            throw new VacationClientException("잘못된 접근입니다. 휴가일 수 음수");
        }

        return vacationDateCount - notWorkingDateCount;
    }

    private static long countNotWorkingDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<LocalDateTime> vacationDateTimes = receiveVacationDateTimes(startDateTime, endDateTime);
        return vacationDateTimes.stream()
                .filter(vacationDateTime -> vacationDateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) || vacationDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .count();
    }

    private static List<LocalDateTime> receiveVacationDateTimes(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime current = startDateTime;

        while (!current.isAfter(endDateTime)) {
            dates.add(current);
            current = current.plusDays(1);
        }

        return dates;
    }
}
