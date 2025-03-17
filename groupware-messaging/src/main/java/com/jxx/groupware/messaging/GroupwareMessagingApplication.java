package com.jxx.groupware.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.jxx.groupware.core", "com.jxx.groupware.messaging"})
@RequiredArgsConstructor
public class GroupwareMessagingApplication {

    private final ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(GroupwareMessagingApplication.class, args);
        log.info("\n=========================================" +
                "\nGroupware Messaging App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
    }
    @EventListener(ContextRefreshedEvent.class)
    public void refreshContext() {
        String[] beanNames = context.getBeanDefinitionNames();

        StringBuilder initializedBeansDescription = new StringBuilder("\n=========================================");

        for (String bean : beanNames) {
            initializedBeansDescription.append("\n Bean : " + bean);
            if (StringUtils.pathEquals(bean, "dataSourceMap")) {
                Map<String, String> dataSourceMap = context.getBean(bean, Map.class);
                log.info("before init {}", dataSourceMap);
            }
        }
        initializedBeansDescription.append("\n=========================================");
        log.info("{}", initializedBeansDescription);
    }

}
