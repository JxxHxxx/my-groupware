package com.jxx.groupware.api.work.dto.response;

public record WorkServiceResponse(
        WorkTicketServiceResponse workTicket,
        WorkDetailServiceResponse workDetail
) {
}
