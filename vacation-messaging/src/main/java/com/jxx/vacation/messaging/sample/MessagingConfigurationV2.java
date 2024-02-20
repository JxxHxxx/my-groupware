package com.jxx.vacation.messaging.sample;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.router.RecipientListRouter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MessagingConfigurationV2 {

    private final EntityManagerFactory entityManagerFactory;

    @Bean(name = "sentQueueChannel1")
    public ExecutorChannel sentQueueChannel1() {
        return new ExecutorChannel(sentQueueTaskExecutor1());
    }
    @Bean(name = "sentQueueChannel2")
    public ExecutorChannel sentQueueChannel2() {
        return new ExecutorChannel(sentQueueTaskExecutor2());
    }

    @Bean(name = "sentQueueTaskExecutor1")
    public TaskExecutor sentQueueTaskExecutor1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // 스레드 풀의 최소 스레드 수
        executor.setMaxPoolSize(200); // 스레드 풀의 최대 스레드 수
        executor.setQueueCapacity(400); // 큐의 최대 용량
        return executor;
    }

    @Bean(name = "sentQueueTaskExecutor2")
    public TaskExecutor sentQueueTaskExecutor2() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // 스레드 풀의 최소 스레드 수
        executor.setMaxPoolSize(200); // 스레드 풀의 최대 스레드 수
        executor.setQueueCapacity(400); // 큐의 최대 용량
        return executor;
    }

    @Bean(name = "inputChannel")
    public QueueChannel inputChannel() {
        QueueChannel queueChannel = new QueueChannel(100);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }

    @Bean(name = "retryQueueChannel")
    public QueueChannel retryQueueChannel() {
        QueueChannel queueChannel = new QueueChannel(10);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }

    @Bean
    public JpaExecutor jpaExecutor() {
        JpaExecutor executor = new JpaExecutor(entityManagerFactory);
        executor.setNativeQuery("SELECT * FROM JXX_MESSAGE_Q " +
                "WHERE MESSAGE_PROCESS_STATUS = 'SENT'" +
                "LIMIT 1");
        return executor;
    }
}
