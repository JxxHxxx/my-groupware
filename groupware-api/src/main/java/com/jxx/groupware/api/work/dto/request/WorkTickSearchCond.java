package com.jxx.groupware.api.work.dto.request;

import java.time.LocalDateTime;

public record WorkTickSearchCond(
        String workTicketId,
        String memberId,
        String companyId,
        String departmentId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String requestTitle,
        String chargeCompanyId,
        String chargeDepartmentId,
        String workStatus

) {
}
