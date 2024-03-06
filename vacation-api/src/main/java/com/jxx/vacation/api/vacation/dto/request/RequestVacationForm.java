package com.jxx.vacation.api.vacation.dto.request;

import com.jxx.vacation.core.vacation.domain.entity.VacationDuration;
import jakarta.validation.constraints.NotNull;

public record RequestVacationForm(
        @NotNull
        String requesterId,
        VacationDuration vacationDuration

) {
}
