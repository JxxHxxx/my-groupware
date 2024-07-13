package com.jxx.vacation.batch.dto.response;

public record TriggerCreateResponse(
        String triggerName,
        String triggerGroup,
        String cronExpression,
        String jobName
) {
}
