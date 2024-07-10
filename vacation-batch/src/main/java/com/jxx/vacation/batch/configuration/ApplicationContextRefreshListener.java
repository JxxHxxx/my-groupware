package com.jxx.vacation.batch.configuration;

import com.jxx.vacation.batch.dto.response.CronTriggerResponse;
import com.jxx.vacation.batch.infra.QuartzExploreMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
//@Component
@RequiredArgsConstructor
public class ApplicationContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {

    private final QuartzExploreMapper quartzExploreMapper;
    private final Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("on application event start");
        ApplicationContext applicationContext = event.getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        QuartzJobConfiguration quartzJobConfiguration = new QuartzJobConfiguration();
        CronTriggerResponse res1 = quartzExploreMapper.findByGroupName("vacation.end.job");
        CronTriggerResponse res2 = quartzExploreMapper.findByGroupName("vacation.start.job");
        if (Objects.nonNull(res1)) {
            Trigger vacationEndJobTrigger = quartzJobConfiguration.vacationEndJobTrigger(res1.getCronExpression());
            beanFactory.registerSingleton("vacation.end.job.vacationEndJobTrigger", vacationEndJobTrigger);
            try {
                scheduler.rescheduleJob(vacationEndJobTrigger.getKey(), vacationEndJobTrigger);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
            // 신규
        } else {
            Trigger vacationEndJobTrigger = quartzJobConfiguration.vacationEndJobTrigger("0 0 0 1 1 ? 2100");
            beanFactory.registerSingleton("vacation.end.job.vacationEndJobTrigger", vacationEndJobTrigger);
            try {
                scheduler.scheduleJob(vacationEndJobTrigger);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }

        if (Objects.nonNull(res2)) {
            Trigger vacationStartJobTrigger = quartzJobConfiguration.vacationStartJobTrigger(res2.getCronExpression());
            beanFactory.registerSingleton("vacation.start.job.vacationStartJobTrigger", vacationStartJobTrigger);
            try {
                scheduler.rescheduleJob(vacationStartJobTrigger.getKey(), vacationStartJobTrigger);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
            // 신규
        } else {
            Trigger vacationStartJobTrigger = quartzJobConfiguration.vacationStartJobTrigger("0 0 0 1 1 ? 2100");
            beanFactory.registerSingleton("vacation.start.job.vacationStartJobTrigger", vacationStartJobTrigger);
            try {
                scheduler.scheduleJob(vacationStartJobTrigger);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
