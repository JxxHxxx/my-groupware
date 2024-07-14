package com.jxx.groupware.batch.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
public class JobSchedulingResponse {
    private final int jobMetaDataPk;
    private final String jobName;
    private final String jobDescription;
    private final LocalDateTime enrolledTime;
    private final String triggerType;
    private final String triggerName;
    private final String triggerGroup;
    private final String triggerState;
    private final String cronExpression;
    private LocalDateTime nextFireTime;
    private boolean schedulingUsed;

    public void setNextFireTime(LocalDateTime nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public void setSchedulingUsed(boolean schedulingUsed) {
        this.schedulingUsed = schedulingUsed;
    }
}
