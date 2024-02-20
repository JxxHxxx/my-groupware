package com.jxx.vacation.messaging.configuration;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.router.RecipientListRouter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MessagingConfiguration {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @Router(inputChannel = "inputChannel")
    public RecipientListRouter router() {
        RecipientListRouter router = new RecipientListRouter();
        router.addRecipient(queueChannel1());
        router.addRecipient(queueChannel2());
        router.addRecipient(queueChannel3());
        router.addRecipient(queueChannel4());
        router.addRecipient(queueChannel5());

        return router;
    }

    @Bean(name = "inputChannel")
    public QueueChannel inputChannel() {
        QueueChannel queueChannel = new QueueChannel(100);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }

    @Bean(name = "sentQueueChannel1")
    public QueueChannel queueChannel1() {
        QueueChannel queueChannel = new QueueChannel(10);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }

    @Bean(name = "sentQueueChannel2")
    public QueueChannel queueChannel2() {
        QueueChannel queueChannel = new QueueChannel(10);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }

    @Bean(name = "sentQueueChannel3")
    public QueueChannel queueChannel3() {
        QueueChannel queueChannel = new QueueChannel(10);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }

    @Bean(name = "sentQueueChannel4")
    public QueueChannel queueChannel4() {
        QueueChannel queueChannel = new QueueChannel(10);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }

    @Bean(name = "sentQueueChannel5")
    public QueueChannel queueChannel5() {
        QueueChannel queueChannel = new QueueChannel(10);
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
