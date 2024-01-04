package com.jxx.vacation.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.jxx.vacation.core","com.jxx.vacation.api"})
public class VacationApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(VacationApiApplication.class, args);
    }

}
