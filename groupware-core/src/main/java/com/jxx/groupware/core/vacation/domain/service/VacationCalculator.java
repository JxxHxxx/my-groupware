package com.jxx.groupware.core.vacation.domain.service;

import com.jxx.groupware.core.vacation.domain.entity.LeaveDeduct;
import com.jxx.groupware.core.vacation.domain.entity.VacationType;
import com.jxx.groupware.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 신청 휴가에 대한 연차 차감 일 수 계산
 */
@Slf4j
public class VacationCalculator {

    private static long DATE_ADJUSTMENTS_VALUE = 1l;
    private static List<DayOfWeek> DEFAULT_WITHOUT_WORKING_DAY = List.of(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY);

    public static float calculateUseLeaveValue(VacationType vacationType, LeaveDeduct leaveDeduct, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (VacationType.HALF_VACATION_TYPE.contains(vacationType)) {
            return 0.5F;
        }

        long vacationDateCount = 0;
        long notWorkingDateCount = 0;
        if (LeaveDeduct.isLeaveDeductVacation(leaveDeduct)) {
            vacationDateCount = ChronoUnit.DAYS.between(startDateTime, endDateTime) + DATE_ADJUSTMENTS_VALUE;
            notWorkingDateCount = countNotWorkingDate(startDateTime, endDateTime);
        }
        final long useLeaveDeduct = vacationDateCount - notWorkingDateCount;

        if (useLeaveDeduct < 0) {
            throw new VacationClientException("잘못된 접근입니다. 휴가일 :" + useLeaveDeduct + "일");
        }

        return useLeaveDeduct;
    }

    private static long countNotWorkingDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<LocalDateTime> vacationDateTimes = new ArrayList<>();
        LocalDateTime current = startDateTime;

        while (!current.isAfter(endDateTime)) {
            vacationDateTimes.add(current);
            current = current.plusDays(1);
        }

        return vacationDateTimes.stream()
                .filter(vacationDateTime -> DEFAULT_WITHOUT_WORKING_DAY.contains(vacationDateTime.getDayOfWeek()))
                .count();
    }

}
