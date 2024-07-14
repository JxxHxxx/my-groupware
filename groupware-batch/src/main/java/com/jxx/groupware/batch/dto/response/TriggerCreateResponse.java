package com.jxx.groupware.batch.dto.response;

public record TriggerCreateResponse(
        String triggerName,
        String triggerGroup,
        String cronExpression,
        String jobName
) {
}
