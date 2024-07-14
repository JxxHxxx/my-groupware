package com.jxx.groupware.batch.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleJobUpdateRequest {
    private final String cronExpression;
    private final String triggerGroup;
}
