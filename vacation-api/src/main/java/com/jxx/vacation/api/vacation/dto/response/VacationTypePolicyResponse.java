package com.jxx.vacation.api.vacation.dto.response;

import com.jxx.vacation.core.vacation.domain.entity.VacationType;

public record VacationTypePolicyResponse(
        String companyId,
        VacationType vacationType,
        Float vacationDay
) {
}
