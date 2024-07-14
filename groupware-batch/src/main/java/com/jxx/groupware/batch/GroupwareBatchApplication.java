package com.jxx.groupware.batch;


import com.jxx.groupware.batch.dto.response.CronTriggerResponse;
import com.jxx.groupware.batch.infra.QuartzExploreMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = {"com.jxx.groupware.core", "com.jxx.groupware.batch"})
public class GroupwareBatchApplication {

    private final ApplicationContext context;
    private final Scheduler scheduler;
    private static List<String> jobBeanNames;
    private final QuartzExploreMapper quartzExploreMapper;

    public static void main(String[] args) {
        SpringApplication.run(GroupwareBatchApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // application Start
        log.info("\n=========================================" +
                "\nGroupware Batch App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");
        jobBeanNames = getJobBeanNames();

        // 스케줄링된 Job 들 확인
        List<CronTriggerResponse> cronTriggerResponses = quartzExploreMapper.findAll();
        for (CronTriggerResponse response : cronTriggerResponses) {
            try {
                Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(response.getTriggerName(), response.getTriggerGroup()));
                JobKey jobKey = trigger.getJobKey();

                LocalDateTime firstFireTime = trigger.getNextFireTime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                log.info("\n=========================================" +
                        "\nQuartz Job Name : {} " +
                        "\nTrigger Name : {}" +
                        "\nFirst Execution Time : {} " +
                        "\nCronExpression : {} " +
                        "\n=========================================", jobKey.getName(), trigger.getKey().getName(), firstFireTime, response.getCronExpression());

            } catch (SchedulerException e) {
                log.warn("DB <-> 애플리케이션 간 트리거 동기화가 되지 않았습니다. triggerName {}", response.getTriggerName());
            }
        }
    }

    /** Quartz 로 스케줄링된 Job 들 확인 **/
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkActivateTrigger() {
        List<CronTriggerResponse> cronTriggerResponses = quartzExploreMapper.findAll();
        for (CronTriggerResponse response : cronTriggerResponses) {
            try {
                Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(response.getTriggerName(), response.getTriggerGroup()));
                JobKey jobKey = trigger.getJobKey();

                LocalDateTime firstFireTime = trigger.getNextFireTime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                log.info("\n=========================================" +
                        "\nQuartz Job Name : {} " +
                        "\nTrigger Name : {}" +
                        "\nNext Execution Time : {} " +
                        "\nCronExpression : {} " +
                        "\n=========================================", jobKey.getName(), trigger.getKey().getName(), firstFireTime, response.getCronExpression());

            } catch (SchedulerException e) {
                log.warn("DB <-> 애플리케이션 간 트리거 동기화가 되지 않았습니다. triggerName {}", response.getTriggerName());
            }
        }
    }

    private List<String> getJobBeanNames() {
        return Arrays.stream(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Job.class))
                .toList();
    }

    /** spring Bean 으로 등록된 Job - 수동 실행을 위해 모니터링 해야 한다. **/
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkActivateJob() {
        BeanFactory beanFactory = context.getAutowireCapableBeanFactory();

        StringBuilder jobActiveLog = new StringBuilder("\n=========================================");
        for (String jobBeanName : jobBeanNames) {
            boolean isActive = beanFactory.containsBean(jobBeanName);
            jobActiveLog.append("\njobBeanName : " + jobBeanName + " isActive : " + isActive);
        }
        jobActiveLog.append("\n=========================================");
        log.info("{}", jobActiveLog);
    }
}
