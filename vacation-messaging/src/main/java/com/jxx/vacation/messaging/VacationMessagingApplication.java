package com.jxx.vacation.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.jxx.vacation.core", "com.jxx.vacation.messaging"})
public class VacationMessagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacationMessagingApplication.class, args);
    }

}
