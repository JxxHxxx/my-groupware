package com.jxx.vacation.batch.configuration;

import com.jxx.vacation.batch.job.leave.config.QuartzVacationEndEventJob;
import com.jxx.vacation.batch.job.vacation.status.config.QuartzVacationStartEventJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzJobConfiguration {

    private final String cronDefault = "0 0 0 1 1 ? 2100"; // 2100년 1월 1일에 실행됨
    @Bean("scheduled.vacation.end.job")
    public JobDetail scheduleVacationEndJob() {
        return JobBuilder
                .newJob(QuartzVacationEndEventJob.class)
                .storeDurably(true)
                .build();
    }
    @Bean
    public Trigger vacationEndJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronDefault);
        return TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(
                        "vacationEndJobTrigger",
                        "vacationEndJobTriggerGroup"))
                .forJob(scheduleVacationEndJob())
                .withSchedule(cronScheduleBuilder)
                .build();
    }
    @Bean("scheduled.vacation.start.job")
    public JobDetail scheduleVacationStartJob() {
        return JobBuilder
                .newJob(QuartzVacationStartEventJob.class)
                .storeDurably(true)
                .build();
    }
    @Bean
    public Trigger vacationStartJobTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronDefault);
        return TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(
                        "vacationStartJobTrigger",
                        "vacationStartJobTriggerGroup"))
                .forJob(scheduleVacationStartJob())
                .withSchedule(cronScheduleBuilder)
                .build();
    }
}
