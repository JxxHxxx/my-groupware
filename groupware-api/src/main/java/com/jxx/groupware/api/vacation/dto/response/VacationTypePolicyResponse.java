package com.jxx.groupware.api.vacation.dto.response;

import com.jxx.groupware.core.vacation.domain.entity.VacationType;

public record VacationTypePolicyResponse(
        String companyId,
        VacationType vacationType,
        Float vacationDay
) {
}
