package com.jxx.vacation.messaging.producer;


import com.jxx.vacation.core.message.MessageQ;
import com.jxx.vacation.core.message.MessageQRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;


import org.springframework.integration.jpa.inbound.JpaPollingChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleMessageProducer {

    private final MessageQRepository messageQRepository;

    @InboundChannelAdapter(channel = "queueChannel", poller = @Poller(fixedDelay = "${poller.interval}"))
    public Message<MessageQ> produce() {
        Optional<MessageQ> messageQOptional = messageQRepository.selectOnlyOne();
        if (messageQOptional.isEmpty()) {
            return null;
        }
        else {
            MessageQ messageQ = messageQOptional.get();
            messageQRepository.deleteById(messageQ.getPk());

            return MessageBuilder
                    .withPayload(messageQ)
                    .build();
        }
    }
}
