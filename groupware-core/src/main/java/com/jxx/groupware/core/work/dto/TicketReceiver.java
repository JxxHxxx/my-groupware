package com.jxx.groupware.core.work.dto;

/** WorkTicket 접수자 **/
public record TicketReceiver(
        String receiverId,
        String receiverCompanyId,
        String receiverDepartmentId
) {
}
