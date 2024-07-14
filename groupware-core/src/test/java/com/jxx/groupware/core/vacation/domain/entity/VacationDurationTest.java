package com.jxx.groupware.core.vacation.domain.entity;

import com.jxx.groupware.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class VacationDurationTest {
    @Test
    void isInVacationDate() {
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 28, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(startDate, endDate, LeaveDeduct.DEDUCT, VacationType.MORE_DAY);
        LocalDateTime betweenDate = LocalDateTime.of(2024, 2, 29, 0, 0);

        assertThatThrownBy(() -> vacationDuration.isAlreadyInVacationDate(betweenDate))
                .isInstanceOf(VacationClientException.class);
    }

    @Test
    void receiveVacationDateTimes() {
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 28, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(startDate, endDate, LeaveDeduct.DEDUCT, VacationType.MORE_DAY);

        List<LocalDateTime> localDateTimes = vacationDuration.receiveVacationDateTimes();

        LocalDateTime betweenDate = LocalDateTime.of(2024, 2, 29, 0, 0);
        //when - then
        log.info("{}" , localDateTimes);
        assertThat(localDateTimes).contains(startDate, endDate, betweenDate);
    }

    @DisplayName("reconciliationVacationDuration 호출 시 " +
            "인자의 종료일자(endDateTime)가 더 미래일 경우 -1을 반환한다.")
    @Test
    void reconciliation_vacation_durations_case_arguments_end_date_time_is_future() {
        // 휴가 종료일 2024-04-25
        LocalDateTime startDateTime1 = LocalDateTime.of(2024, 4, 24, 0, 0, 0);
        LocalDateTime endDateTime1 = LocalDateTime.of(2024, 4, 25, 0, 0, 0);
        VacationDuration vacationDuration1 = new VacationDuration(startDateTime1, endDateTime1, LeaveDeduct.DEDUCT, VacationType.MORE_DAY);

        // 휴가 종료일 2024-04-26
        LocalDateTime startDateTime2 = LocalDateTime.of(2024, 4, 25, 0, 0, 0);
        LocalDateTime endDateTime2 = LocalDateTime.of(2024, 4, 26, 0, 0, 0);
        VacationDuration vacationDuration2 = new VacationDuration(startDateTime2, endDateTime2, LeaveDeduct.DEDUCT, VacationType.MORE_DAY);

        // 2024-04-25 은 2024-04-26 보다 과거이기 때문에 -1 이 박힌다.
        int result = vacationDuration1.reconciliationVacationDurations(vacationDuration2);
        int pastFlag  = -1;

        assertThat(result).isEqualTo(pastFlag);
    }

    @DisplayName("reconciliationVacationDuration 호출 시 " +
            "인자의 종료일자(endDateTime)가 더 과거일 경우 1을 반환한다.")
    @Test
    void reconciliation_vacation_durations_case_arguments_end_date_time_is_past() {
        // 휴가 종료일 2024-04-25
        LocalDateTime startDateTime1 = LocalDateTime.of(2024, 4, 24, 0, 0, 0);
        LocalDateTime endDateTime1 = LocalDateTime.of(2024, 4, 25, 0, 0, 0);
        VacationDuration vacationDuration1 = new VacationDuration(startDateTime1, endDateTime1, LeaveDeduct.DEDUCT, VacationType.MORE_DAY);

        // 휴가 종료일 2024-04-24
        LocalDateTime startDateTime2 = LocalDateTime.of(2024, 4, 23, 0, 0, 0);
        LocalDateTime endDateTime2 = LocalDateTime.of(2024, 4, 24, 0, 0, 0);
        VacationDuration vacationDuration2 = new VacationDuration(startDateTime2, endDateTime2, LeaveDeduct.DEDUCT, VacationType.MORE_DAY);

        // 2024-04-25 은 2024-04-24 보다 미래기 때문에 1 이 박힌다.
        int result = vacationDuration1.reconciliationVacationDurations(vacationDuration2);
        int futureFlag  = 1;

        assertThat(result).isEqualTo(futureFlag);
    }

}