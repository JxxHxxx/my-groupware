package com.jxx.groupware.api.work.dto.request;

public record WorkTicketPlanRequest(
        String receiverCompanyId,
        String receiverDepartmentId,
        String receiverId,
        String receiverName,
        String workPlanContent
) {
}
