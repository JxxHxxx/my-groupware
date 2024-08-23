package com.jxx.groupware.api.work.application;

public record WorkTicketCreateEvent(
        String chargeCompanyId,
        String chargeDepartmentId
) {
}
