package com.jxx.vacation.messaging.producer;


import com.jxx.vacation.core.message.MessageQ;
import com.jxx.vacation.core.message.MessageQRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleMessageProducer {

    private final MessageQRepository messageQRepository;

    @Transactional
    @InboundChannelAdapter(channel = "queueChannel", poller = @Poller(fixedDelay = "${poller.interval}"))
    public Message<MessageQ> produce() {
        Optional<MessageQ> messageQOptional = messageQRepository.selectOnlyOne();
        if (messageQOptional.isEmpty()) {
            return null;
        }
        else {
            MessageQ messageQ = messageQOptional.get();
            messageQ.startProduce(); // dirty checking

            log.info("PRODUCE : message PK={} channel=queueChannel", messageQ.getPk());
            return MessageBuilder
                    .withPayload(messageQ)
                    .build();
        }
    }
}
