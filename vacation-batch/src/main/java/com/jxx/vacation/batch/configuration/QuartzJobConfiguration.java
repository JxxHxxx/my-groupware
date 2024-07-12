package com.jxx.vacation.batch.configuration;

import com.jxx.vacation.batch.job.leave.config.QuartzVacationEndEventJob;
import com.jxx.vacation.batch.job.vacation.status.config.QuartzVacationStartEventJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.jxx.vacation.batch.GlobalJobConstant.VACATION_END_JOB_NAME;
import static com.jxx.vacation.batch.GlobalJobConstant.VACATION_START_JOB_NAME;

@Configuration
public class QuartzJobConfiguration {

    @Bean("scheduled.vacation.start.job")
    public JobDetail scheduleVacationStartJob() {
        return JobBuilder
                .newJob(QuartzVacationStartEventJob.class)
                .storeDurably(true)
                .withIdentity(VACATION_START_JOB_NAME)
                .withDescription("Quartz 연차 시작 배치 잡")
                .build();
    }
    @Bean("scheduled.vacation.end.job")
    public JobDetail scheduleVacationEndJob() {
        return JobBuilder
                .newJob(QuartzVacationEndEventJob.class)
                .storeDurably(true)
                .withIdentity(VACATION_END_JOB_NAME)
                .withDescription("Quartz 연차 종료 배치 잡")
                .build();
    }
}
