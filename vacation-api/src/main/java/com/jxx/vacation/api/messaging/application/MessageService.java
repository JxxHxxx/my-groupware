package com.jxx.vacation.api.messaging.application;

import com.jxx.vacation.core.message.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageQResultRepository messageQResultRepository;
    private final MessageQRepository messageQRepository;

    // 메시지 재처리
    // 1. 완변 성공된 메시지는 다시 재처리할 수 없어야 한다.
    @Transactional
    public void retry(Long messageQResultPk) {
        MessageQResult messageQResult = messageQResultRepository.findById(messageQResultPk)
                .orElseThrow(() -> new IllegalArgumentException());

        MessageQ messageQ = MessageQ.builder()
                .retryId(messageQResult.getOriginalMessagePk())
                .messageProcessStatus(MessageProcessStatus.RETRY)
                .messageDestination(messageQResult.getMessageDestination())
                .body(messageQResult.getBody())
                .build();

        messageQRepository.save(messageQ);
    }

}
