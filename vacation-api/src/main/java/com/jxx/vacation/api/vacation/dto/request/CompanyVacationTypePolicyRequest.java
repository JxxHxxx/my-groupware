package com.jxx.vacation.api.vacation.dto.request;

import java.util.List;

public record CompanyVacationTypePolicyRequest(
        String adminId,
        List<CompanyVacationTypePolicyForm> form
) {
}
