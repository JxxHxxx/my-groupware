package com.jxx.vacation.api.vacation.dto.response;

import com.jxx.vacation.core.vacation.domain.entity.LeaveDeduct;

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
