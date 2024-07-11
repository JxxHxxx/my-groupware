package com.jxx.vacation.batch.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
public class SchedulingResponse {
    private final String jobName;
    private final String jobDescription;
    private final String triggerName;
    private final String triggerGroup;
    private final String triggerState;
    private final String cronExpression;
    private LocalDateTime nextFireTime;
    private boolean used;

    public void setNextFireTime(LocalDateTime nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
