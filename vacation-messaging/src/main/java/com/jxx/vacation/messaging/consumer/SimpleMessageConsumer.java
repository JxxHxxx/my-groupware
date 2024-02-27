package com.jxx.vacation.messaging.consumer;

import com.jxx.vacation.core.message.*;
import com.jxx.vacation.messaging.application.MessageService;
import com.jxx.vacation.messaging.infra.ApprovalRepository;
import com.jxx.vacation.messaging.infra.VacationConfirmModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.ServiceActivators;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jxx.vacation.core.message.MessageConst.RETRY_HEADER;
import static com.jxx.vacation.core.message.MessageProcessStatus.FAIL;
import static com.jxx.vacation.core.message.MessageProcessStatus.SUCCESS;

@Slf4j
@MessageEndpoint
@RequiredArgsConstructor
public class SimpleMessageConsumer {

    @Qualifier("vacationMessageService")
    private final MessageService messageService;


    @Transactional
    @ServiceActivators({
            @ServiceActivator(inputChannel = "sentQueueChannel1"), @ServiceActivator(inputChannel = "sentQueueChannel1"),
            @ServiceActivator(inputChannel = "sentQueueChannel1"), @ServiceActivator(inputChannel = "sentQueueChannel1"),
            @ServiceActivator(inputChannel = "sentQueueChannel1"), @ServiceActivator(inputChannel = "sentQueueChannel1"),
            @ServiceActivator(inputChannel = "sentQueueChannel1"), @ServiceActivator(inputChannel = "sentQueueChannel1"),
            @ServiceActivator(inputChannel = "sentQueueChannel1"), @ServiceActivator(inputChannel = "sentQueueChannel1")
    })
    public void consumeSentMessage1(List<Message<MessageQ>> message) {
        processes(message, "1");
    }

    @Transactional
    @ServiceActivators({
            @ServiceActivator(inputChannel = "sentQueueChannel2"), @ServiceActivator(inputChannel = "sentQueueChannel2"),
            @ServiceActivator(inputChannel = "sentQueueChannel2"), @ServiceActivator(inputChannel = "sentQueueChannel2"),
            @ServiceActivator(inputChannel = "sentQueueChannel2"), @ServiceActivator(inputChannel = "sentQueueChannel2"),
            @ServiceActivator(inputChannel = "sentQueueChannel2"), @ServiceActivator(inputChannel = "sentQueueChannel2"),
            @ServiceActivator(inputChannel = "sentQueueChannel2"), @ServiceActivator(inputChannel = "sentQueueChannel1")
    })
    public void consumeSentMessage2(List<Message<MessageQ>> message) {
        processes(message, "1");
    }

    private void processes(List<Message<MessageQ>> messages, String channelNum) {
        for (Message<MessageQ> message : messages) {
            messageService.process(message);
            log.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> HISTORY START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +
                    "\nChannel:sentQueueChannel{}" +
                    "\nMessage:{}" +
                    "\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< HISTORY  END  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", channelNum, message);
        }
    }


    @Transactional
    @ServiceActivator(inputChannel = "retryQueueChannel")
    public void consumeRetryMessage(Message<MessageQ> message) {
        messageService.retry(message);
            log.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> HISTORY START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +
                    "\nChannel:retryQueueChannel" +
                    "\nMessage:{}" +
                    "\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< HISTORY  END  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", message);
    }
}
