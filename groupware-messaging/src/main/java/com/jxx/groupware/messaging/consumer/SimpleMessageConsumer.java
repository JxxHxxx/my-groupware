package com.jxx.groupware.messaging.consumer;

import com.jxx.groupware.core.messaging.domain.queue.MessageProcessType;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.messaging.application.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.ServiceActivators;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// TODO : 예외 발생 시, 큰 문제가 생긴다.. 채널 수 만큼 입력됨

@Slf4j
@MessageEndpoint
@RequiredArgsConstructor
public class SimpleMessageConsumer {
    private final BeanFactory beanFactory;

    @Transactional
    @ServiceActivators({
            @ServiceActivator(inputChannel = "sentQueueChannel1")
    })
    public void consumeSentMessage1(List<Message<MessageQ>> message) {
        processes(message, "1");
    }

    @Transactional
    @ServiceActivators({
            @ServiceActivator(inputChannel = "sentQueueChannel2")
    })
    public void consumeSentMessage2(List<Message<MessageQ>> message) {
        processes(message, "2");
    }

    public void processes(List<Message<MessageQ>> messages, String channelNum) {
        for (Message<MessageQ> message : messages) {
            MessageService<MessageQ> messageService = adaptMessageServiceBean(message);
            messageService.process(message);
            String messageServiceImplClassName = messageService.getClass().getSimpleName();
            log.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CONSUME START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +
                    "\nChannel:sentQueueChannel{}" +
                    "\nMessageService implementation : {}" +
                    "\nMessage:{}" +
                    "\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CONSUME  END  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", channelNum, messageServiceImplClassName, message);

        }
    }


    @Transactional
    @ServiceActivator(inputChannel = "retryQueueChannel")
    public void consumeRetryMessage(Message<MessageQ> message) {
        MessageService<MessageQ> messageService = adaptMessageServiceBean(message);
        messageService.retry(message);
        String messageServiceImplClassName = messageService.getClass().getSimpleName();
        log.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CONSUME START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +
                "\nChannel:retryQueueChannel" +
                "\nMessageService implementation : {}" +
                "\nMessage:{}" +
                "\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CONSUME  END  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", messageServiceImplClassName, message);
    }

    private MessageService<MessageQ> adaptMessageServiceBean(Message<MessageQ> message) {
        MessageService<MessageQ> messageService;
        MessageProcessType messageProcessType = message.getPayload().getMessageProcessType();

        if (messageProcessType.isDBProcessType()) {
            messageService = beanFactory.getBean("vacationMessageService", MessageService.class);
        } else if (messageProcessType.isRestProcessType()) { // REST API
            messageService = beanFactory.getBean("restApiMessageService", MessageService.class);
        } else if (messageProcessType.isRelationalDatabaseProcessType()) {
            messageService = beanFactory.getBean("rdbMessageService", MessageService.class);
        } else {
            throw new RuntimeException();
        }
        return messageService;
    }
}
