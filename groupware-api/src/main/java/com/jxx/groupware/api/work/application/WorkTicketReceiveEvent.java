package com.jxx.groupware.api.work.application;

public record WorkTicketReceiveEvent(
        String receiverId,
        String receiverCompanyId,
        String receiverDepartmentId,
        String chargeCompanyId,
        String chargeDepartmentId
) {
}
