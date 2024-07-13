package com.jxx.vacation.api.vacation.dto.request;

import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;

public record ConfirmStatusChangeRequest(
        String requestSystem,
        VacationStatus vacationStatus

) {
}
