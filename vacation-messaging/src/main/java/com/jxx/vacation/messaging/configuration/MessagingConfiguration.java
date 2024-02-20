package com.jxx.vacation.messaging.configuration;

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
//@Configuration
@RequiredArgsConstructor
public class MessagingConfiguration {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @Router(inputChannel = "inputChannel")
    public RecipientListRouter router() {
        RecipientListRouter router = new RecipientListRouter();
        router.addRecipient(sentQueueChannel1());
        router.addRecipient(sentQueueChannel2());
        router.addRecipient(sentQueueChannel3());
        router.addRecipient(sentQueueChannel4());
        router.addRecipient(sentQueueChannel5());

        return router;
    }

    @Bean(name = "sentQueueChannel1")
    public ExecutorChannel sentQueueChannel1() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel2")
    public ExecutorChannel sentQueueChannel2() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel3")
    public ExecutorChannel sentQueueChannel3() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel4")
    public ExecutorChannel sentQueueChannel4() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel5")
    public ExecutorChannel sentQueueChannel5() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel6")
    public ExecutorChannel sentQueueChannel6() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel7")
    public ExecutorChannel sentQueueChannel7() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel8")
    public ExecutorChannel sentQueueChannel8() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel9")
    public ExecutorChannel sentQueueChannel9() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel10")
    public ExecutorChannel sentQueueChannel10() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel11")
    public ExecutorChannel sentQueueChannel11() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel12")
    public ExecutorChannel sentQueueChannel12() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel13")
    public ExecutorChannel sentQueueChannel13() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel14")
    public ExecutorChannel sentQueueChannel14() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel15")
    public ExecutorChannel sentQueueChannel15() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel16")
    public ExecutorChannel sentQueueChannel16() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel17")
    public ExecutorChannel sentQueueChannel17() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel18")
    public ExecutorChannel sentQueueChannel18() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel19")
    public ExecutorChannel sentQueueChannel19() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel20")
    public ExecutorChannel sentQueueChannel20() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel21")
    public ExecutorChannel sentQueueChannel21() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel22")
    public ExecutorChannel sentQueueChannel22() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel23")
    public ExecutorChannel sentQueueChannel23() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel24")
    public ExecutorChannel sentQueueChannel24() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel25")
    public ExecutorChannel sentQueueChannel25() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel26")
    public ExecutorChannel sentQueueChannel26() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel27")
    public ExecutorChannel sentQueueChannel27() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel28")
    public ExecutorChannel sentQueueChannel28() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel29")
    public ExecutorChannel sentQueueChannel29() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel30")
    public ExecutorChannel sentQueueChannel30() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel31")
    public ExecutorChannel sentQueueChannel31() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel32")
    public ExecutorChannel sentQueueChannel32() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel33")
    public ExecutorChannel sentQueueChannel33() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel34")
    public ExecutorChannel sentQueueChannel34() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel35")
    public ExecutorChannel sentQueueChannel35() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel36")
    public ExecutorChannel sentQueueChannel36() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel37")
    public ExecutorChannel sentQueueChannel37() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel38")
    public ExecutorChannel sentQueueChannel38() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel39")
    public ExecutorChannel sentQueueChannel39() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel40")
    public ExecutorChannel sentQueueChannel40() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel41")
    public ExecutorChannel sentQueueChannel41() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel42")
    public ExecutorChannel sentQueueChannel42() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel43")
    public ExecutorChannel sentQueueChannel43() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel44")
    public ExecutorChannel sentQueueChannel44() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel45")
    public ExecutorChannel sentQueueChannel45() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    @Bean(name = "sentQueueChannel46")
    public ExecutorChannel sentQueueChannel46() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }
    // 채널 2에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel47")
    public ExecutorChannel sentQueueChannel47() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 3에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel48")
    public ExecutorChannel sentQueueChannel48() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 4에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel49")
    public ExecutorChannel sentQueueChannel49() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }

    // 채널 5에 대한 설정 (반복)
    @Bean(name = "sentQueueChannel50")
    public ExecutorChannel sentQueueChannel50() {
        return new ExecutorChannel(sentQueueTaskExecutor());
    }



    @Bean
    public TaskExecutor sentQueueTaskExecutor() {
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

//    @Bean(name = "sentQueueChannel1")
//    public QueueChannel queueChannel1() {
//        QueueChannel queueChannel = new QueueChannel(10);
//        queueChannel.afterPropertiesSet();
//        return queueChannel;
//    }
//
//    @Bean(name = "sentQueueChannel2")
//    public QueueChannel queueChannel2() {
//        QueueChannel queueChannel = new QueueChannel(10);
//        queueChannel.afterPropertiesSet();
//        return queueChannel;
//    }
//
//    @Bean(name = "sentQueueChannel3")
//    public QueueChannel queueChannel3() {
//        QueueChannel queueChannel = new QueueChannel(10);
//        queueChannel.afterPropertiesSet();
//        return queueChannel;
//    }
//
//    @Bean(name = "sentQueueChannel4")
//    public QueueChannel queueChannel4() {
//        QueueChannel queueChannel = new QueueChannel(10);
//        queueChannel.afterPropertiesSet();
//        return queueChannel;
//    }
//
//    @Bean(name = "sentQueueChannel5")
//    public QueueChannel queueChannel5() {
//        QueueChannel queueChannel = new QueueChannel(10);
//        queueChannel.afterPropertiesSet();
//        return queueChannel;
//    }

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
