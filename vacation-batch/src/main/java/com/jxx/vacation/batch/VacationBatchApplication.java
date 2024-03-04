package com.jxx.vacation.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.jxx.vacation.core","com.jxx.vacation.batch"})
public class VacationBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(VacationBatchApplication.class, args);
        log.info("\n=========================================" +
                "\nVacation Batch App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");
    }

}
