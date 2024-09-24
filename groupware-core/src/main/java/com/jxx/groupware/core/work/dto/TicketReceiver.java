package com.jxx.groupware.core.work.dto;

public record TicketReceiver(
        String receiverId,
        String receiverCompanyId,
        String receiverDepartmentId
) {
}
