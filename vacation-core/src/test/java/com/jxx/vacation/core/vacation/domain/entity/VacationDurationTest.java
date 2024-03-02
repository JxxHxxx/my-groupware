package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class VacationDurationTest {

    @Test
    void calculate() {
        LocalDateTime today = LocalDateTime.now();
        VacationDuration vd1 = new VacationDuration(VacationType.MORE_DAY, today, today);
        assertThat(vd1.calculateDate()).isEqualTo(1l);


        VacationDuration vd2 = new VacationDuration(VacationType.MORE_DAY, today, today.plusDays(1l));
        assertThat(vd2.calculateDate()).isEqualTo(2l);

        LocalDateTime start = LocalDateTime.of(2024, 2, 29, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 1, 0, 0);

        VacationDuration vd3 = new VacationDuration(VacationType.MORE_DAY, start, end);
        assertThat(vd3.calculateDate()).isEqualTo(2l);
    }

    @Test
    void isInVacationDate() {
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 28, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);
        LocalDateTime betweenDate = LocalDateTime.of(2024, 2, 29, 0, 0);

        assertThatThrownBy(() -> vacationDuration.isInVacationDate(betweenDate, ""))
                .isInstanceOf(VacationClientException.class);
    }

    @Test
    void receiveVacationDateTimes() {
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 28, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);

        List<LocalDateTime> localDateTimes = vacationDuration.receiveVacationDateTimes();

        LocalDateTime betweenDate = LocalDateTime.of(2024, 2, 29, 0, 0);
        //when - then
        log.info("{}" , localDateTimes);
        assertThat(localDateTimes).contains(startDate, endDate, betweenDate);
    }

}