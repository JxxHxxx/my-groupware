package com.jxx.groupware.api.messaging.application;

import com.jxx.groupware.core.common.pagination.PageService;
import com.jxx.groupware.api.messaging.dto.request.MessageQResultSearchCondition;
import com.jxx.groupware.api.messaging.dto.response.MessageQResultResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageQResultResponseV2;
import com.jxx.groupware.api.messaging.query.MessageQResultMapper;
import com.jxx.groupware.core.messaging.domain.MessageProcessStatus;
import com.jxx.groupware.core.messaging.domain.MessageQ;
import com.jxx.groupware.core.messaging.domain.MessageQResult;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.messaging.infra.MessageQResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageQResultRepository messageQResultRepository;
    private final MessageQRepository messageQRepository;
    private final MessageQResultMapper messageQResultMapper;

    public PageImpl<MessageQResultResponse> findProcessFailMessages(int page, int size, String startDate, String endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.of(0, 0, 0));
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.of(23, 59, 59));
        List<MessageQResult> messageQResults = messageQResultRepository.findMessageQResult(startDateTime, endDateTime);

        // 성공 이력이 있는 Original Message Pk 제외하기
        List<MessageQResult> filterMessage = new ArrayList<>();
        Set<Long> failedOriginalMessagePks = new HashSet<>();
        for (MessageQResult messageQResult : messageQResults) {
            // 중복으로 Message Result가 담겨지는 것을 막기 위해
            if (failedOriginalMessagePks.contains(messageQResult.getOriginalMessagePk())) {
                continue;
            }
            // 실패 메시지라면 최초에 담고 filterMessage 에도 담는다.
            if (messageQResult.isFail()) {
                failedOriginalMessagePks.add(messageQResult.getOriginalMessagePk());
                filterMessage.add(messageQResult);
            }
        }


        // 아직 성공 처리되지 않은 메시지들의 모음
        List<MessageQResultResponse> notSucceedResponses = filterMessage.stream()
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
        PageService pageService = new PageService(page, size);
        return pageService.convertToPage(notSucceedResponses);
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
                .messageProcessType(messageQResult.getMessageProcessType())
                .messageDestination(messageQResult.getMessageDestination())
                .body(messageQResult.getBody())
                .build();

        MessageQ savedMessageQ = messageQRepository.save(messageQ);
    }

    /** V1 **/
    public PageImpl<MessageQResultResponseV2> findSuccessMessageQResult(MessageQResultSearchCondition searchCondition, int page, int size) {
        // 전체 메시지를 불러오도록 조건 변경
//        searchCondition.setMessageProcessStatus(MessageProcessStatus.SUCCESS);
        List<MessageQResultResponseV2> response = messageQResultMapper.search(searchCondition);
        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int total = response.size();
        int end = Math.min((start + pageRequest.getPageSize()), total);

        PageImpl<MessageQResultResponseV2> result = null;
        try {
            result = new PageImpl<>(response.subList(start, end), pageRequest, total);
        } catch (IllegalArgumentException e) {
            // TODO ExceptionHandler 처리
            throw new MessageClientException(e);
        }

        return result;
    }

}
