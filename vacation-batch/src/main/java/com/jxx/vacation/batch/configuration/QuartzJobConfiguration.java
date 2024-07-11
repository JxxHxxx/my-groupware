package com.jxx.vacation.batch.configuration;

import com.jxx.vacation.batch.job.leave.config.QuartzVacationEndEventJob;
import com.jxx.vacation.batch.job.vacation.status.config.QuartzVacationStartEventJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobConfiguration {

    @Bean("scheduled.vacation.start.job")
    public JobDetail scheduleVacationStartJob() {
        return JobBuilder
                .newJob(QuartzVacationStartEventJob.class)
                .storeDurably(true)
                .withIdentity("vacation.start.job")
                .withDescription("Quartz 연차 시작 배치 잡")
                .build();
    }
    @Bean("scheduled.vacation.end.job")
    public JobDetail scheduleVacationEndJob() {
        return JobBuilder
                .newJob(QuartzVacationEndEventJob.class)
                .storeDurably(true) // DB 저장 X
                .withIdentity("vacation.end.job")
                .withDescription("Quartz 연차 종료 배치 잡")
                .build();
    }
}
