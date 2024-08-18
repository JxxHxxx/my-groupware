package com.jxx.groupware.api.work.application;

import com.jxx.groupware.core.work.domain.WorkRequester;

public record WorkTicketCreateRequest(
        String chargeCompanyId,
        String chargeDepartmentId,
        String requestTitle,
        String requestContent,
        WorkRequester workRequester
) {
}
