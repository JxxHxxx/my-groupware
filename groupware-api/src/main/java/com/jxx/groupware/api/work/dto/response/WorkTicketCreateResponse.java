package com.jxx.groupware.api.work.dto.response;

import com.jxx.groupware.core.work.domain.WorkRequester;
import com.jxx.groupware.core.work.domain.WorkStatus;

import java.time.LocalDateTime;

public record WorkTicketCreateResponse(
        Long workTicketPk,
        String workTicketId,
        WorkStatus workStatus,
        LocalDateTime createdTime,
        String chargeCompanyId,
        String chargeDepartmentId,
        LocalDateTime modifiedTime,
        String requestTitle,
        String requestContent,
        WorkRequester workRequester
) {
}
