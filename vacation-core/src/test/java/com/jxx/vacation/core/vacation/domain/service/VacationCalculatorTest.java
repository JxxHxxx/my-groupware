package com.jxx.vacation.core.vacation.domain.service;


import com.jxx.vacation.core.vacation.domain.entity.LeaveDeduct;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class VacationCalculatorTest {

    @DisplayName("useLeaveDeduct는 워킹 데이만큼 감소한다. " +
            "현재 기본적으로 평일(월,화,수,목,금)은 워킹 데이 주말(토, 일)은 휴일로 구분되어 있다.")
    @Test
    void calculate_use_leave_deduct_case_all_working_day() {
        LocalDateTime startDate = LocalDateTime.of(2024, 4, 22, 0, 0, 0); // 월요일
        LocalDateTime endDate = LocalDateTime.of(2024, 4, 23, 0, 0, 0); // 화요일(+1)
        //when
        float useLeaveDeduct = VacationCalculator.calculateUseLeaveValue(VacationType.MORE_DAY, LeaveDeduct.DEDUCT, startDate, endDate);
        //then
        assertThat(useLeaveDeduct).isEqualTo(2f);
    }

    @DisplayName("useLeaveDeduct 는 워킹 데이만큼 감소한다. 휴가 신청일에 휴일이 포함되어 있을 경우, useLeaveDeduct 값에서 차감된다.")
    @Test
    void calculate_use_leave_deduct_case_include_without_working_day() {
        LocalDateTime startDate = LocalDateTime.of(2024, 4, 21, 0, 0, 0); // 일요일
        LocalDateTime endDate = LocalDateTime.of(2024, 4, 22, 0, 0, 0); // 월요일(+1)
        //when
        float useLeaveDeduct = VacationCalculator.calculateUseLeaveValue(VacationType.MORE_DAY, LeaveDeduct.DEDUCT, startDate, endDate);
        //then
        assertThat(useLeaveDeduct).isEqualTo(1f);
    }

    @DisplayName("useLeaveDeduct 는 워킹 데이만큼 감소한다. 휴가 시작일이 종료일보다 더 먼 미래일 경우 " +
            "VacationClientException 이 발생한다.")
    @Test
    void calculate_use_leave_deduct_case_include_case() {
        LocalDateTime startDate = LocalDateTime.of(2024, 4, 25, 0, 0, 0); // 일요일
        LocalDateTime endDate = LocalDateTime.of(2024, 4, 22, 0, 0, 0); // 월요일(+1)
        //when
        assertThatThrownBy(() -> VacationCalculator.calculateUseLeaveValue(VacationType.MORE_DAY, LeaveDeduct.DEDUCT, startDate, endDate))
                .isInstanceOf(VacationClientException.class).hasMessageContaining("잘못된 접근입니다. 휴가일 :-2일");


    }
}