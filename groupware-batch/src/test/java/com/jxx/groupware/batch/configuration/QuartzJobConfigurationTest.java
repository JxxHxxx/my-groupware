package com.jxx.groupware.batch.configuration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;
import java.time.LocalDateTime;

@Slf4j
class QuartzJobConfigurationTest {

    @Test
    void cronToLocalDateTime() {
        String cronEx = "0 0 0 * * *";

        CronExpression cronExpression = CronExpression.parse(cronEx);
        LocalDateTime next = cronExpression.next(LocalDateTime.now());
        log.info("next : {}" , next);
        log.info("nextTime : {}" , next.toLocalTime());
    }
}