package com.jxx.vacation.batch.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleJobUpdateRequest {
    private final String originalJobBeanName; // 스프링 배치 잡 빈 이름
    private final String cronExpression; // 크론표현식
    private final String triggerName;
    private final String triggerGroup;
}
