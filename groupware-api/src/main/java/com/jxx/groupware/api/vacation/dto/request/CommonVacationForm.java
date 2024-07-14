package com.jxx.groupware.api.vacation.dto.request;


import java.time.LocalDate;
import java.util.List;

public record CommonVacationForm(
        String companyId,
        boolean mustApproval,
        boolean deducted,
        List<LocalDate> vacationDates
) {
}
