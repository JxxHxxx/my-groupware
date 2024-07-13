package com.jxx.vacation.messaging.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChannelConfiguration {

    @Bean(name = "sentQueueChannel1")
    public ExecutorChannel sentQueueChannel1() {
        ExecutorChannel executorChannel = new ExecutorChannel(sentQueueTaskExecutor1());
        return executorChannel;
    }
    @Bean(name = "sentQueueChannel2")
    public ExecutorChannel sentQueueChannel2() {
        ExecutorChannel executorChannel = new ExecutorChannel(sentQueueTaskExecutor2());
        return executorChannel;
    }

    @Bean(name = "sentQueueTaskExecutor1")
    public TaskExecutor sentQueueTaskExecutor1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // 스레드 풀의 최소 스레드 수
        executor.setMaxPoolSize(200); // 스레드 풀의 최대 스레드 수
        executor.setQueueCapacity(1000); // 큐의 최대 용량
        executor.setThreadNamePrefix("consumer-1-");
        return executor;
    }

    @Bean(name = "sentQueueTaskExecutor2")
    public TaskExecutor sentQueueTaskExecutor2() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // 스레드 풀의 최소 스레드 수
        executor.setMaxPoolSize(200); // 스레드 풀의 최대 스레드 수
        executor.setQueueCapacity(1000); // 큐의 최대 용량
        executor.setThreadNamePrefix("consumer-2-");
        return executor;
    }

    @Bean(name = "retryQueueChannel")
    public QueueChannel retryQueueChannel() {
        QueueChannel queueChannel = new QueueChannel(100);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }
}
