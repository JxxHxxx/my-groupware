package com.jxx.vacation.messaging.consumer;

import com.jxx.vacation.core.message.*;
import com.jxx.vacation.messaging.infra.ApprovalRepository;
import com.jxx.vacation.messaging.infra.VacationConfirmForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        log.info("Message info : {}", message);
        MessageQ messageQ = message.getPayload();
        Map<String, Object> body = messageQ.getBody();

        String companyId = (String) body.get("company_id");
        String departmentId = (String) body.get("department_id");
        String approvalStatus = (String) body.get("approval_status");
        String confirmDocumentId = String.valueOf(body.get("confirm_document_id"));
        String requesterId = (String) body.get("requester_id");

        VacationConfirmForm vacationConfirmForm = new VacationConfirmForm(companyId, departmentId, approvalStatus, confirmDocumentId, requesterId);
        approvalRepository.insert(vacationConfirmForm);
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
        log.info("================= END ====================");
    }
}
