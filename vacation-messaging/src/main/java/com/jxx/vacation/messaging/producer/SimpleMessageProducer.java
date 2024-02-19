package com.jxx.vacation.messaging.producer;


import com.jxx.vacation.core.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.jxx.vacation.core.message.MessageConst.*;
import static com.jxx.vacation.core.message.MessageProcessStatus.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleMessageProducer {

    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;

    @Transactional
    @InboundChannelAdapter(channel = "retryQueueChannel", poller = @Poller(fixedDelay = "${poller.interval.retry}"))
    public Message<MessageQ> produceRetryMessage() {
        Optional<MessageQ> messageQOptional = messageQRepository.selectRetryOne();
        if (messageQOptional.isEmpty()) {
            return null;
        }
        MessageQ messageQ = messageQOptional.get();
        Long originalMessageQPk = messageQ.getRetryId();

        boolean alreadySuccess = messageQResultRepository.findByOriginalMessagePk(originalMessageQPk)
                .stream()
                .anyMatch(mqr -> SUCCESS.equals(mqr.getMessageProcessStatus()));
        if (alreadySuccess) {
            log.warn("[ALREADY][original message pk={}]", originalMessageQPk);

            messageQRepository.deleteById(messageQ.getPk());
            messageQResultRepository.save(createAlreadySuccessMessageQResult(messageQ, originalMessageQPk));
            return null;
        }

        log.info("[RETRY][original message pk={}]", originalMessageQPk);
        messageQ.startProduce(); // dirty checking
        return MessageBuilder
                .withPayload(messageQ)
                .setHeader(RETRY_HEADER, originalMessageQPk)
                .build();

    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce() {
        Optional<MessageQ> messageQOptional = messageQRepository.selectSentOne();
        if (messageQOptional.isEmpty()) {
            return null;
        }
        MessageQ messageQ = messageQOptional.get();

        // 최초
        log.info("[SENT][message pk={}]", messageQ.getPk());
        messageQ.startProduce(); // dirty checking
        return MessageBuilder
                .withPayload(messageQ)
                .build();
    }

//    @Transactional
//    @InboundChannelAdapter(channel = "queueChannel", poller = @Poller(fixedDelay = "${poller.interval}"))
//    public Message<MessageQ> produce() {
//        Optional<MessageQ> messageQOptional = messageQRepository.selectOnlyOne();
//        if (messageQOptional.isEmpty()) {
//            return null;
//        }
//        MessageQ messageQ = messageQOptional.get();
//        // 재시도
//        if (RETRY.equals(messageQ.getMessageProcessStatus())) {
//            Long originalMessageQPk = messageQ.getRetryId();
//            log.info("[RETRY][original message pk={}]", originalMessageQPk);
//
//            boolean alreadySuccess = messageQResultRepository.findByOriginalMessagePk(originalMessageQPk)
//                    .stream()
//                    .anyMatch(mqr -> SUCCESS.equals(mqr.getMessageProcessStatus()));
//            if (alreadySuccess) {
//                log.warn("[ALREADY][original message pk={}]", originalMessageQPk);
//
//                messageQRepository.deleteById(messageQ.getPk());
//                messageQResultRepository.save(createAlreadySuccessMessageQResult(messageQ, originalMessageQPk));
//                return null;
//            }
//            messageQ.startProduce(); // dirty checking
//            return MessageBuilder
//                    .withPayload(messageQ)
//                    .setHeader(RETRY_HEADER, originalMessageQPk)
//                    .build();
//        }
//        // 최초
//        log.info("[SENT][message pk={}]", messageQ.getPk());
//        messageQ.startProduce(); // dirty checking
//        return MessageBuilder
//                .withPayload(messageQ)
//                .build();
//    }

    private static MessageQResult createAlreadySuccessMessageQResult(MessageQ messageQ, Long originalMessageQPk) {
        return MessageQResult.builder()
                .messageProcessStatus(ALREADY)
                .processStartTime(messageQ.getProcessStartTime())
                .processEndTime(LocalDateTime.now())
                .messageDestination(messageQ.getMessageDestination())
                .eventTime(messageQ.getEventTime())
                .originalMessagePk(originalMessageQPk)
                .body(messageQ.getBody())
                .build();
    }
}
