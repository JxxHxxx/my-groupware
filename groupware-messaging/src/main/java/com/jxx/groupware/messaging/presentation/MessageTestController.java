package com.jxx.groupware.messaging.presentation;

import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.messaging.application.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MessageTestController {

    private final MessageService<MessageQ> messageService;
    public MessageTestController(@Qualifier("RdbMessageService") MessageService<MessageQ> messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/msg/test")
    public ResponseEntity<?> createMessage(@RequestBody Map<String, String> messageRequest) {
        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.valueOf(messageRequest.get("messageDestination")))
                .build();
        Message<MessageQ> message = MessageBuilder.withPayload(messageQ).build();
        messageService.process(message);

        return ResponseEntity.ok("메시지 전송 성공");
    }
}
