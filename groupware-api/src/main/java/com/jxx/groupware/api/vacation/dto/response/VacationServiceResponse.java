package com.jxx.groupware.api.vacation.dto.response;

import com.jxx.groupware.core.vacation.domain.dto.VacationDurationDto;
import com.jxx.groupware.core.vacation.domain.entity.VacationStatus;

import java.util.List;

public record VacationServiceResponse(
        Long vacationId,
        String requesterId,
        String requesterName,
        List<VacationDurationDto> vacationDuration,
        VacationStatus vacationStatus
) {
}
