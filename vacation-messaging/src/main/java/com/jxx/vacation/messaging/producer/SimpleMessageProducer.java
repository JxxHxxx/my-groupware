package com.jxx.vacation.messaging.producer;


import com.jxx.vacation.core.message.MessageQ;
import com.jxx.vacation.core.message.MessageQRepository;
import com.jxx.vacation.core.message.MessageQResult;
import com.jxx.vacation.core.message.MessageQResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jxx.vacation.core.message.MessageConst.RETRY_HEADER;
import static com.jxx.vacation.core.message.MessageProcessStatus.ALREADY;
import static com.jxx.vacation.core.message.MessageProcessStatus.SUCCESS;


@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleMessageProducer {

    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;

    @Value("${messaging.produce.limit-size}")
    private int limitSize;

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel1", poller = @Poller(fixedDelay = "${poller.interval.sent}", maxMessagesPerPoll = "500"))
    public List<Message<MessageQ>> produce1() {
        return processes();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel2", poller = @Poller(fixedDelay = "${poller.interval.sent}", maxMessagesPerPoll = "500"))
    public List<Message<MessageQ>> produce2() {
        return processes();
    }

    private List<Message<MessageQ>> processes() {
        List<MessageQ> messageQs = messageQRepository.findWithLimit(limitSize);
        if (messageQs.isEmpty()) {
            return null;
        }

        List<Message<MessageQ>> messageContainer = new ArrayList<>();

        for (MessageQ messageQ : messageQs) {
            messageQ.startProduce();

            Message<MessageQ> qMessage = MessageBuilder
                    .withPayload(messageQ)
                    .build();
            messageContainer.add(qMessage);
        }
        List<Long> messagePks = messageQs.stream().map(MessageQ::getPk).toList();
        log.info("[SENT][MID:{}]", messagePks);
        return messageContainer;
    }

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
            log.warn("[ALREADY][MID:{}]", originalMessageQPk);

            messageQRepository.deleteById(messageQ.getPk());
            messageQResultRepository.save(createAlreadySuccessMessageQResult(messageQ, originalMessageQPk));
            return null;
        }

        log.info("[RETRY][MID:{}]", originalMessageQPk);
        messageQ.startProduce(); // dirty checking
        return MessageBuilder
                .withPayload(messageQ)
                .setHeader(RETRY_HEADER, originalMessageQPk)
                .build();
    }

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
