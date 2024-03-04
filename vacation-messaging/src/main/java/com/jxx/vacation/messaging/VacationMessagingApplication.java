package com.jxx.vacation.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.jxx.vacation.core", "com.jxx.vacation.messaging"})
public class VacationMessagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacationMessagingApplication.class, args);
        log.info("\n=========================================" +
                "\nVacation Messaging App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");
    }

}
