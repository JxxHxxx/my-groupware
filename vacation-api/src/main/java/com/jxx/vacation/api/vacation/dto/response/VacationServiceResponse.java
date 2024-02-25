package com.jxx.vacation.api.vacation.dto.response;

import com.jxx.vacation.core.vacation.domain.entity.VacationDuration;
import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;

public record VacationServiceResponse(
        Long vacationId,
        String requesterId,
        String requesterName,
        VacationDuration vacationDuration,
        VacationStatus vacationStatus
) {
}
