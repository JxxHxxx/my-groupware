package com.jxx.vacation.messaging.configuration;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.jpa.core.JpaExecutor;

import javax.sql.DataSource;
import java.security.PublicKey;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MessagingConfiguration {

    private final EntityManagerFactory entityManagerFactory;

    @Bean(name = "queueChannel")
    public QueueChannel queueChannel() {
        QueueChannel queueChannel = new QueueChannel(2);
        queueChannel.afterPropertiesSet();
        return queueChannel;
    }

    @Bean
    public JpaExecutor jpaExecutor() {
        JpaExecutor executor = new JpaExecutor(entityManagerFactory);|
        executor.setJpaQuery("SELECT m FROM JXX_MESSAGE_Q m");
        return executor;
    }
}
