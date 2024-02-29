package com.jxx.vacation.core.vacation.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class VacationDurationTest {

    @Test
    void calculate() {
        LocalDateTime today = LocalDateTime.now();
        VacationDuration vd1 = new VacationDuration(VacationType.MORE_DAY, today, today);
        assertThat(vd1.calculateDate()).isEqualTo(1l);


        VacationDuration vd2 = new VacationDuration(VacationType.MORE_DAY, today, today.plusDays(1l));
        assertThat(vd2.calculateDate()).isEqualTo(2l);

        LocalDateTime start = LocalDateTime.of(2024, 02, 29, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 03, 1, 0, 0);

        VacationDuration vd3 = new VacationDuration(VacationType.MORE_DAY, start, end);
        assertThat(vd3.calculateDate()).isEqualTo(2l);
    }

}