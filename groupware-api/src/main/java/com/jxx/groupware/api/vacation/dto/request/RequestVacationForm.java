package com.jxx.groupware.api.vacation.dto.request;

import com.jxx.groupware.core.vacation.domain.dto.RequestVacationDuration;
import com.jxx.groupware.core.vacation.domain.dto.validation.constraint.DurationsConstraint;
import com.jxx.groupware.core.vacation.domain.entity.LeaveDeduct;
import com.jxx.groupware.core.vacation.domain.entity.VacationType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RequestVacationForm(
        @NotNull
        String requesterId,
        VacationType vacationType,
        LeaveDeduct leaveDeduct,
        @DurationsConstraint
        List<RequestVacationDuration> requestVacationDurations,
        String title,
        String reason,
        String requesterName,
        String delegatorId,
        String delegatorName,
        String departmentId,
        String departmentName

) {
}
