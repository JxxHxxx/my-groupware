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

@Slf4j
@Component
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
        Trigger vacationEndJobTrigger = quartzJobConfiguration.vacationEndJobTrigger(res1.getCronExpression());
        Trigger vacationStartJobTrigger = quartzJobConfiguration.vacationStartJobTrigger(res2.getCronExpression());

        beanFactory.registerSingleton("vacationEndJobTrigger", vacationEndJobTrigger);
        beanFactory.registerSingleton("vacationStartJobTrigger", vacationStartJobTrigger);

        try {
            scheduler.rescheduleJob(vacationEndJobTrigger.getKey(), vacationEndJobTrigger);
            scheduler.rescheduleJob(vacationStartJobTrigger.getKey(),vacationStartJobTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
