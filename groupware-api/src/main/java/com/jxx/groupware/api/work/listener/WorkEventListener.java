package com.jxx.groupware.api.work.listener;


import com.jxx.groupware.core.ConfirmCreateForm;
import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessType;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkEventListener {

    private final MessageQRepository messageQRepository;

    @Async("${event.executor.name}")
    @Transactional
    @EventListener(CreateConfirmThroughRestApiEvent.class)
    public void handleWorkEvent(CreateConfirmThroughRestApiEvent event) {
        try {
            ConfirmCreateForm confirmCreateForm = event.confirmCreateForm();
            Map<String, Object> payload = new HashMap<>();
            payload.put("requestBody", confirmCreateForm);
            payload.put("method", event.method());
            payload.put("baseUrl", event.baseUrl());
            payload.put("path", event.path());

            MessageQ messageQ = MessageQ.builder()
                    .messageDestination(MessageDestination.CONFIRM)
                    .messageProcessStatus(MessageProcessStatus.SENT)
                    .messageProcessType(MessageProcessType.REST)
                    .body(payload)
                    .build();

            messageQRepository.save(messageQ);
        } catch (Exception e) {
            log.info("Fail Create MessageQ requestConfirmEvent {}", event);
            log.error("Error : ", e);
        }
    }
}
