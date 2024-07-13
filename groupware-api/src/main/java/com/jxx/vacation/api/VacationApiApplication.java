package com.jxx.vacation.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.jxx.vacation.core", "com.jxx.vacation.api"})
@RequiredArgsConstructor
public class VacationApiApplication {

    private final ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(VacationApiApplication.class, args);
        log.info("\n=========================================" +
                "\nVacation Api App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");
    }

    @EventListener(ContextRefreshedEvent.class)
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
