package com.jxx.groupware.api.vacation.dto.request;

import java.util.List;

public record CompanyVacationTypePolicyRequest(
        String companyId,
        List<VacationTypePolicyForm> vacationTypePolicyForms
) {
}
