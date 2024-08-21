package com.jxx.groupware.api.work.dto.request;

public record WorkTicketReceiveRequest(
        String receiverCompanyId,
        String receiverDepartmentId,
        String receiverId,
        String receiverName
) {
}
