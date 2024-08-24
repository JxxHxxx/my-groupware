package com.jxx.groupware.api.work.dto.request;

public record WorkTicketAnalyzeRequest(
        String receiverCompanyId,
        String receiverDepartmentId,
        String receiverId,
        String receiverName
) {
}
