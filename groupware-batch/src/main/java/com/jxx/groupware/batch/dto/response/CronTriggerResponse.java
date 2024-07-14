package com.jxx.groupware.batch.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CronTriggerResponse {
    private final String triggerName;
    private final String triggerGroup;
    private final String cronExpression;
}
