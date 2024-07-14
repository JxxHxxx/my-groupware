package com.jxx.groupware.api.vacation.dto.response;

import com.jxx.groupware.core.vacation.domain.entity.LeaveDeduct;

import java.util.List;

public record CommonVacationServiceResponse(
        int leaveDeductedUserNum,
        int totalUseLeaveValue,
        boolean mustApproval,
        boolean deducted,
        LeaveDeduct leaveDeduct,
        List<VacationServiceResponse> vacations
) {
}
