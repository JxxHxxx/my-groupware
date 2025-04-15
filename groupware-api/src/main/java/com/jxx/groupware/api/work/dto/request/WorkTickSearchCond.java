package com.jxx.groupware.api.work.dto.request;

import com.jxx.groupware.core.work.domain.WorkStatus;

import java.time.LocalDateTime;
import java.util.List;

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
        List<WorkStatus> workStatus,
        List<WorkStatus> notWorkStatus

) {
}
