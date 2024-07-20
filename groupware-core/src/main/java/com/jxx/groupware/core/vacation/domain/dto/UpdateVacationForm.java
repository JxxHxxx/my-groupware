package com.jxx.groupware.core.vacation.domain.dto;

import com.jxx.groupware.core.vacation.domain.dto.validation.constraint.DurationsConstraint;

import java.util.List;

/*사유, 대리자, 휴가 기간 */
public record UpdateVacationForm(
        String delegatorId,
        String delegatorName,
        String reason,
        @DurationsConstraint
        List<RequestVacationDuration> requestVacationDurations,
        Long contentPk

) {
}
