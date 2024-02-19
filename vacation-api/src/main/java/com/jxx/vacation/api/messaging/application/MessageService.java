package com.jxx.vacation.api.messaging.application;

import com.jxx.vacation.api.messaging.dto.response.MessageQResultResponse;
import com.jxx.vacation.core.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageQResultRepository messageQResultRepository;
    private final MessageQRepository messageQRepository;

    public PageImpl<MessageQResultResponse> findNotSucceededMessages(int page, int size) {
        List<MessageQResult> messageQResults = messageQResultRepository.test();

        // 성공 이력이 있는 Original Message Pk 제외하기
        List<Long> retriedMessageQ = messageQResults.stream()
                .filter(messageQResult -> messageQResult.getMessageProcessStatus().equals(MessageProcessStatus.SUCCESS))
                .map(MessageQResult::getOriginalMessagePk)
                .toList();

        // 아직 성공 처리되지 않은 메시지들의 모음
        List<MessageQResultResponse> filtered = messageQResults.stream()
                .filter(messageQResult -> !retriedMessageQ.contains(messageQResult.getOriginalMessagePk()))
                .filter(messageQResult -> !messageQResult.getOriginalMessagePk().equals(Long.MIN_VALUE))
                .map(mqr -> new MessageQResultResponse(
                        mqr.getPk(),
                        mqr.getOriginalMessagePk(),
                        mqr.getMessageDestination(),
                        mqr.getBody(),
                        mqr.getMessageProcessStatus(),
                        mqr.getEventTime(),
                        mqr.getProcessStartTime(),
                        mqr.getProcessEndTime()))
                .toList();

        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), filtered.size());
        return new PageImpl<>(filtered.subList(start, end), pageRequest, filtered.size());
    }


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
