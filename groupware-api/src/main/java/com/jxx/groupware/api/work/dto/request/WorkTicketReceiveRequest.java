package com.jxx.groupware.api.work.dto.request;

public record WorkTicketReceiveRequest(
        String receiverId,
        String receiverName
) {
}
