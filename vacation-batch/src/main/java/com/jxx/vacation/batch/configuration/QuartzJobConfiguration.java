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

    @Bean("scheduled.vacation.start.job")
    public JobDetail scheduleVacationStartJob() {
        return JobBuilder
                .newJob(QuartzVacationStartEventJob.class)
                .storeDurably(true)
                .build();
    }

    public Trigger vacationEndJobTrigger(String cronExpression) {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        return TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(
                        "vacationEndJobTrigger",
                        "vacation.end.job"))
                .forJob(scheduleVacationEndJob())
                .withSchedule(cronScheduleBuilder)
                .build();
    }

//    @Bean(name = "vacation.end.job.vacationEndJobTrigger")
    public Trigger VacationEndJobTriggerV2() {
        return vacationEndJobTrigger("0 0 0 1 1 ? 2100");
    }

    public Trigger vacationStartJobTrigger(String cronExpression) {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        return TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(
                        "vacationStartJobTrigger",
                        "vacation.start.job"))
                .forJob(scheduleVacationStartJob())
                .withSchedule(cronScheduleBuilder)
                .build();
    }

//    @Bean(name = "vacation.start.job.vacationStartJobTrigger")
    public Trigger vacationStartJobTriggerV2() {
        return vacationStartJobTrigger("0 0 0 1 1 ? 2100");
    }
}
