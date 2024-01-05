package com.jxx.vacation.messaging.producer;

import com.jxx.vacation.core.domain.MessageQ;
import com.jxx.vacation.core.domain.MessageQRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.jpa.inbound.JpaPollingChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleMessageProducer {

    private final QueueChannel queueChannel;
    private final JpaExecutor executor;

    private static final Random random = new Random();

    private final MessageQRepository messageQRepository;
    private static final String[] fruits = {"strawberry", "apple", "melon", "orange"};

    @InboundChannelAdapter(channel = "routingChannel", poller = @Poller(fixedRate = "{}"))
    public MessageSource<?> produce() {
       return new JpaPollingChannelAdapter(executor);
    }
}
