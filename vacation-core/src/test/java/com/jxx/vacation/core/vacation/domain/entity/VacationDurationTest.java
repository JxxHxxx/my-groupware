package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class VacationDurationTest {

    @Test
    void calculate() {
        LocalDateTime today = LocalDateTime.now();
        VacationDuration vd1 = new VacationDuration(today, today, LeaveDeduct.DEDUCT);
        assertThat(vd1.calculateDate()).isEqualTo(1l);


        VacationDuration vd2 = new VacationDuration(today, today.plusDays(1l), LeaveDeduct.DEDUCT);
        assertThat(vd2.calculateDate()).isEqualTo(2l);

        LocalDateTime start = LocalDateTime.of(2024, 2, 29, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 1, 0, 0);

        VacationDuration vd3 = new VacationDuration(start, end, LeaveDeduct.DEDUCT);
        assertThat(vd3.calculateDate()).isEqualTo(2l);
    }

    @DisplayName("휴가 기간에 주말(토, 일)이 포함되어 있으면 차감 일수에선 제외된다.")
    @Test
    void calculate_include_weekend_case() {
        // 목, 금, 토, 일 -> 2일
        LocalDateTime start = LocalDateTime.of(2024, 2, 29, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 3, 0, 0);

        VacationDuration vd3 = new VacationDuration(start, end, LeaveDeduct.DEDUCT);
        assertThat(vd3.calculateDate()).isEqualTo(2l);
    }

    @Test
    void isInVacationDate() {
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 28, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(startDate, endDate, LeaveDeduct.DEDUCT);
        LocalDateTime betweenDate = LocalDateTime.of(2024, 2, 29, 0, 0);

        assertThatThrownBy(() -> vacationDuration.isAlreadyInVacationDate(betweenDate))
                .isInstanceOf(VacationClientException.class);
    }

    @Test
    void receiveVacationDateTimes() {
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 28, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(startDate, endDate, LeaveDeduct.DEDUCT);

        List<LocalDateTime> localDateTimes = vacationDuration.receiveVacationDateTimes();

        LocalDateTime betweenDate = LocalDateTime.of(2024, 2, 29, 0, 0);
        //when - then
        log.info("{}" , localDateTimes);
        assertThat(localDateTimes).contains(startDate, endDate, betweenDate);
    }

}