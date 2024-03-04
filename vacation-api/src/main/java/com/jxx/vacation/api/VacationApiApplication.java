package com.jxx.vacation.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.jxx.vacation.core","com.jxx.vacation.api"})
public class VacationApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(VacationApiApplication.class, args);
        log.info("\n=========================================" +
                "\nVacation Api App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");
    }

}
