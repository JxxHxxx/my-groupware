package com.jxx.groupware.api.vacation.dto.request;

import com.jxx.groupware.core.vacation.domain.entity.VacationStatus;

public record ConfirmStatusChangeRequest(
        String requestSystem,
        VacationStatus vacationStatus

) {
}
