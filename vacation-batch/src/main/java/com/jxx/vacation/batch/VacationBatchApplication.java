package com.jxx.vacation.batch;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = {"com.jxx.vacation.core", "com.jxx.vacation.batch"})
public class VacationBatchApplication {

    private final ApplicationContext context;
    private static List<String> JobBeanNames;
    private static List<String> JobTriggerNames;

    public static void main(String[] args) {
        SpringApplication.run(VacationBatchApplication.class, args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        // application Start
        log.info("\n=========================================" +
                "\nVacation Batch App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");

        JobBeanNames = getJobBeanNames();

        for (String jobBeanName : JobBeanNames) {
            log.info("\n=========================================" +
                    "\nJob Name : {} " +
                    "\nFirst Execution Time : ..." +
                    "\nPeriod : ..." +
                    "\n=========================================", jobBeanName);
        }

        log.info("\nTotal enrolled job : {}", JobBeanNames.size());

        JobTriggerNames = getJobTriggerNames();
        for (String triggerName : JobTriggerNames) {
            Trigger trigger = context.getBean(triggerName, Trigger.class);

            LocalDateTime fireTime = trigger.getNextFireTime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            log.info("\n=========================================" +
                    "\nTrigger Name : {} " +
                    "\nNext Execution Time : {}" +
                    "\nPeriod : ..." +
                    "\n=========================================", triggerName, fireTime);
        }

    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void checkActivateTrigger() {
        StringBuilder jobActiveLog = new StringBuilder("\n=========================================");

        Map<String, Trigger> triggers = context.getBeansOfType(Trigger.class);
        Set<String> triggerNames = triggers.keySet();
        for (String triggerName : triggerNames) {
            Trigger trigger = context.getBean(triggerName, Trigger.class);
            LocalDateTime fireTime = trigger.getNextFireTime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            jobActiveLog.append("\ntriggerName : " + triggerName + " next execute time : " + fireTime);
        }

        jobActiveLog.append("\n=========================================");
        log.info("{}", jobActiveLog);
    }

    private List<String> getJobBeanNames() {
        return Arrays.stream(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Job.class))
                .toList();
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void checkActivateJob() {
        BeanFactory beanFactory = context.getAutowireCapableBeanFactory();

        StringBuilder jobActiveLog = new StringBuilder("\n=========================================");
        for (String jobBeanName : JobBeanNames) {
            boolean isActive = beanFactory.containsBean(jobBeanName);
            jobActiveLog.append("\njobBeanName : " + jobBeanName + " isActive : " + isActive);
        }
        jobActiveLog.append("\n=========================================");
        log.info("{}", jobActiveLog);

    }

    private List<String> getJobTriggerNames() {
        return Arrays.stream(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Trigger.class))
                .toList();
    }
}
