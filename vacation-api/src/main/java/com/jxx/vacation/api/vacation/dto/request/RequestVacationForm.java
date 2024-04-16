package com.jxx.vacation.api.vacation.dto.request;

import com.jxx.vacation.core.vacation.domain.RequestVacationDuration;
import com.jxx.vacation.core.vacation.domain.entity.LeaveDeduct;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RequestVacationForm(
        @NotNull
        String requesterId,
        VacationType vacationType,
        LeaveDeduct leaveDeduct,
        List<RequestVacationDuration> requestVacationDurations

) {
}
