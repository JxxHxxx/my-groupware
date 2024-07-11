package com.jxx.vacation.batch.configuration;

import com.jxx.vacation.batch.dto.response.CronTriggerResponse;
import com.jxx.vacation.batch.infra.QuartzExploreMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
//@Component 이거 할 필요 없음 Trigger 는 빈으로 등록할 이유가 없을듯
@RequiredArgsConstructor
public class ApplicationContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {

    private final QuartzExploreMapper quartzExploreMapper;
    private final Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("on application event start");
        ApplicationContext applicationContext = event.getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        List<CronTriggerResponse> cronTriggerResponses = quartzExploreMapper.findAll();

        if (cronTriggerResponses.isEmpty()) {
            log.info("등록되어 있는 트리거 목록이 존재하지 않습니다");
        }

        for (CronTriggerResponse cronTriggerResponse : cronTriggerResponses) {
            registerBeanAndScheduleTrigger(beanFactory, cronTriggerResponse);
        }

    }
    private void registerBeanAndScheduleTrigger(
            ConfigurableListableBeanFactory beanFactory,
            CronTriggerResponse cronTriggerResponse) {
        // 조회 결과가 있는 경우 - 즉 신규 트리거가 아닌 경우

        log.info("기존 트리거 {} 빈 등록 및 스케줄러 설정을 시작합니다.", cronTriggerResponse.getTriggerName());
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronTriggerResponse.getCronExpression());

        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(cronTriggerResponse.getTriggerName(), cronTriggerResponse.getTriggerGroup()))
                .forJob(cronTriggerResponse.getTriggerName().replace("Trigger", ""))
                .withSchedule(cronScheduleBuilder)
                .build();

        beanFactory.registerSingleton(cronTriggerResponse.getTriggerName(), cronTrigger);

        try {
            scheduler.rescheduleJob(cronTrigger.getKey(), cronTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }
}
