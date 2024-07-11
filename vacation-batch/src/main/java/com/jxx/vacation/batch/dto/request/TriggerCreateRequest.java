package com.jxx.vacation.batch.dto.request;
public record TriggerCreateRequest(
        String jobName, // jobDetail 클래스로 등록된 빈이여야함
        String cronExpression
) {

}
