package com.jxx.groupware.api.work.dto.request;

import com.jxx.groupware.core.work.dto.TicketReceiver;

/** 작업 티켓 접수자의 티켓 반려 요청 **/
public record WorkTicketRejectRequest(
        TicketReceiver ticketReceiver,
        String rejectReason
) {
}
