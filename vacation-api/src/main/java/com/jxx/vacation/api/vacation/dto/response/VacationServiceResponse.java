package com.jxx.vacation.api.vacation.dto.response;

import com.jxx.vacation.core.vacation.domain.VacationDurationDto;
import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;

import java.util.List;

public record VacationServiceResponse(
        Long vacationId,
        String requesterId,
        String requesterName,
        List<VacationDurationDto> vacationDuration,
        VacationStatus vacationStatus
) {
}
