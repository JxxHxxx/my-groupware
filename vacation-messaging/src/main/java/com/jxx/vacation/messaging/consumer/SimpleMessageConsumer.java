package com.jxx.vacation.messaging.consumer;

import com.jxx.vacation.core.message.*;
import com.jxx.vacation.messaging.infra.ApprovalRepository;
import com.jxx.vacation.messaging.infra.VacationConfirmModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@MessageEndpoint
@RequiredArgsConstructor
public class SimpleMessageConsumer {

    @Qualifier("approvalNamedParameterJdbcTemplate")
    private final ApprovalRepository approvalRepository;
    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;

    @Transactional
    @ServiceActivator(inputChannel = "queueChannel")
    public void process(Message<MessageQ> message) {
        log.info("=================START====================");
        MessageQ messageQ = message.getPayload();
        Map<String, Object> body = messageQ.getBody();
        try {
            log.info("Message info : {}", message);

            String confirmStatus = (String) body.get("confirm_status");
            String confirmDocumentId = String.valueOf(body.get("confirm_document_id"));
            String createSystem = (String) body.get("create_system");
            String documentType = (String) body.get("document_type");
            String companyId = (String) body.get("company_id");
            String departmentId = (String) body.get("department_id");
            String requesterId = (String) body.get("requester_id");
            LocalDateTime createTime = convertToCreateTime(body);

            VacationConfirmModel vacationConfirmModel = new VacationConfirmModel(
                    confirmStatus, confirmDocumentId, createSystem, createTime, documentType, companyId, departmentId, requesterId);
            approvalRepository.insert(vacationConfirmModel);

            // 리팩터링 대상 - 메시치 처리 결과 이력 남기기 테이블
            messageQRepository.deleteById(messageQ.getPk());

            MessageQResult messageQResult = MessageQResult.builder()
                    .messageProcessStatus(MessageProcessStatus.SUCCESS)
                    .processStartTime(messageQ.getProcessStartTime())
                    .processEndTime(LocalDateTime.now())
                    .messageDestination(messageQ.getMessageDestination())
                    .eventTime(messageQ.getEventTime())
                    .originalMessagePk(messageQ.getPk())
                    .body(body)
                    .build();

            messageQResultRepository.save(messageQResult);
        } catch (Exception e) {

            log.info("메시지 변환 중 오류가 발생했습니다.", e);
            messageQRepository.deleteById(messageQ.getPk());

            MessageQResult messageQResult = MessageQResult.builder()
                    .messageProcessStatus(MessageProcessStatus.FAIL)
                    .processStartTime(messageQ.getProcessStartTime())
                    .processEndTime(LocalDateTime.now())
                    .messageDestination(messageQ.getMessageDestination())
                    .eventTime(messageQ.getEventTime())
                    .originalMessagePk(messageQ.getPk())
                    .body(body)
                    .build();

            messageQResultRepository.save(messageQResult);
        }
        log.info("================= END ====================");
    }

    private static LocalDateTime convertToCreateTime(Map<String, Object> body) {
        List<Integer> createTimes = (ArrayList) body.get("create_time");
        Integer year = createTimes.get(0);
        Integer month = createTimes.get(1);
        Integer dayOfMonth = createTimes.get(2);
        Integer hour = createTimes.get(3);
        Integer minute = createTimes.get(4);
        Integer second = createTimes.get(5);
        Integer nanoOfSecond = createTimes.get(6);

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
    }
}
