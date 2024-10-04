package com.jxx.groupware.api.vacation.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CommonVacationForm(
        @NotNull(message = "회사 코드(comapnyId)는 null 일 수 없습니다.")
        String companyId,
        @NotNull(message = "승인 필요 여부(mustApporval)는 null 일 수 없습니다.")
        Boolean mustApproval,
        @NotNull(message = "연차 차감 여부(deducted)는 null 일 수 없습니다.")
        Boolean deducted,
        @NotEmpty(message = "적어도 한 개의 공동 연차 일자를 추가하세요.")
        List<LocalDate> vacationDates
) {
}
