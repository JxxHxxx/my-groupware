package com.jxx.groupware.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.jxx.groupware.core", "com.jxx.groupware.messaging"})
@RequiredArgsConstructor
public class GroupwareMessagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupwareMessagingApplication.class, args);
        log.info("\n=========================================" +
                "\nGroupware Messaging App Start Success" +
                "\nDevelop JxxHxx " +
                "\n=========================================");
    }

}
