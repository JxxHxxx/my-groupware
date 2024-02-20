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
import java.util.List;
import java.util.Optional;

import static com.jxx.vacation.core.message.MessageConst.*;
import static com.jxx.vacation.core.message.MessageProcessStatus.*;


@Slf4j
//@Component
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
    @InboundChannelAdapter(channel = "sentQueueChannel1", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce1_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel2", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce2_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel3", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce3_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel4", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce4_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel5", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce5_1() {
        return process();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel6", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce6_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel7", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce7_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel8", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce8_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel9", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce9_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel10", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce10_1() {
        return process();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel11", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce11_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel12", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce12_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel13", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce13_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel14", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce14_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel15", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce15_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel16", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce16_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel17", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce17_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel18", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce18_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel19", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce19_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel20", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce20_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel21", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce21_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel22", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce22_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel23", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce23_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel24", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce24_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel25", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce25_1() {
        return process();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel26", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce26_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel27", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce27_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel28", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce28_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel29", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce29_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel30", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce30_1() {
        return process();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel31", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce31_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel32", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce32_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel33", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce33_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel34", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce34_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel35", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce35_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel36", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce36_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel37", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce37_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel38", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce38_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel39", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce39_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel40", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce40_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel41", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce41_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel42", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce42_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel43", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce43_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel44", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce44_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel45", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce45_1() {
        return process();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel46", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce46_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel47", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce47_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel48", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce48_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel49", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce49_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel50", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce50_1() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel1", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce1_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel2", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce2_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel3", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce3_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel4", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce4_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel5", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce5_2() {
        return process();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel6", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce6_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel7", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce7_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel8", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce8_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel9", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce9_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel10", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce10_2() {
        return process();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel11", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce11_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel12", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce12_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel13", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce13_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel14", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce14_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel15", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce15_2() {
        return process();
    }
    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel16", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce16_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel17", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce17_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel18", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce18_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel19", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce19_2() {
        return process();
    }

    @Transactional
    @InboundChannelAdapter(channel = "sentQueueChannel20", poller = @Poller(fixedDelay = "${poller.interval.sent}"))
    public Message<MessageQ> produce20_2() {
        return process();
    }


    private Message<MessageQ> process() {
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
