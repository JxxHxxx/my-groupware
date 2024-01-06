package com.jxx.vacation.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.vacation.core.message.MessageQ;
import com.jxx.vacation.messaging.infra.ApprovalRepository;
import com.jxx.vacation.messaging.infra.VacationConfirmForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@MessageEndpoint
@RequiredArgsConstructor
public class SimpleMessageConsumer {

    @Qualifier("approvalNamedParameterJdbcTemplate")
    private final ApprovalRepository approvalRepository;

    @Transactional
    @ServiceActivator(inputChannel = "queueChannel")
    public void process(Message<MessageQ> messageQ) {
        log.info("=================START====================");
        log.info("Message info : {}", messageQ);
        Map<String, Object> body = messageQ.getPayload().getBody();

        String companyId = (String) body.get("company_id");
        String departmentId = (String) body.get("department_id");
        String approvalStatus = (String) body.get("approval_status");

        VacationConfirmForm vacationConfirmForm = new VacationConfirmForm(companyId, departmentId, approvalStatus);
        approvalRepository.insert(vacationConfirmForm);

        log.info("================= END ====================");
    }
}
