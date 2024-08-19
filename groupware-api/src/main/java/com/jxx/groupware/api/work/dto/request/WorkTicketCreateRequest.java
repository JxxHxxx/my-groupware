package com.jxx.groupware.api.work.dto.request;

import com.jxx.groupware.core.work.domain.WorkRequester;

public record WorkTicketCreateRequest(
        String chargeCompanyId,
        String chargeDepartmentId,
        String requestTitle,
        String requestContent,
        WorkRequester workRequester
) {
}
