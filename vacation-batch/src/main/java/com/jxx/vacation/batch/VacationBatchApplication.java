package com.jxx.vacation.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Arrays;
import java.util.List;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = {"com.jxx.vacation.core","com.jxx.vacation.batch"})
public class VacationBatchApplication {

    private final ApplicationContext context;
    private static List<String> applicationRefreshAfterJobNames;

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

        applicationRefreshAfterJobNames = getJobBeanNames();
        List<String> jobBeanNames = getJobBeanNames();

        for (String jobBeanName : jobBeanNames) {
            log.info("\n=========================================" +
                    "\nJob Name : {} " +
                    "\nFirst Execution Time : ..." +
                    "\nPeriod : ..." +
                    "\n=========================================",jobBeanName);
        }

        log.info("\nTotal enrolled job : {}", jobBeanNames.size());
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkActivateJob() {
        BeanFactory beanFactory = context.getAutowireCapableBeanFactory();

        StringBuilder jobActiveLog = new StringBuilder("\n=========================================");
        for (String jobBeanName : applicationRefreshAfterJobNames) {
            boolean isActive = beanFactory.containsBean(jobBeanName);
            jobActiveLog.append("\njobBeanName : " + jobBeanName + " isActive : " + isActive);
        }
        jobActiveLog.append("\n=========================================");
        log.info("{}", jobActiveLog);
    }

    private List<String> getJobBeanNames() {
        return Arrays.stream(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Job.class))
                .toList();
    }
}
