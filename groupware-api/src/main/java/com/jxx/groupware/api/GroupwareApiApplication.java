package com.jxx.groupware.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.jxx.groupware.core", "com.jxx.groupware.api"})
@RequiredArgsConstructor
public class GroupwareApiApplication {

    private final ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(GroupwareApiApplication.class, args);
        log.info("\n=========================================" +
                "\nGroupware Api App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<String> beanNames = Arrays.stream(context.getBeanDefinitionNames()).toList();

        StringBuilder initializedBeansDescription = new StringBuilder("\n=========================================");

        for (String bean : beanNames) {
            initializedBeansDescription.append("\n Bean : " + bean);
        }
        initializedBeansDescription.append("\n=========================================");
        log.info("{}", initializedBeansDescription);

    }
}
