package com.jxx.vacation.api.vacation.listener;


import com.jxx.vacation.core.message.*;
import com.jxx.vacation.core.message.domain.MessageDestination;
import com.jxx.vacation.core.message.domain.MessageProcessStatus;
import com.jxx.vacation.core.message.domain.MessageQ;
import com.jxx.vacation.core.message.infra.MessageQRepository;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmMessageForm;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacationEventListener {

    private final MessageQRepository messageQRepository;

    @Async("${event.executor.name}")
    @Transactional(propagation = Propagation.REQUIRED)
    @EventListener(VacationCreatedEvent.class)
    public void listen(VacationCreatedEvent createdEvent) {
        try {
            MessageQ messageQ = createMessage(createdEvent);
            messageQRepository.save(messageQ);
        } catch (Exception e) {
            log.info("Fail Create MessageQ createdEvent {}", createdEvent);
            log.error("Error : ", e);
        }
    }

    private static MessageQ createMessage(VacationCreatedEvent createdEvent) {
        Vacation vacation = createdEvent.vacation();
        Organization organization = createdEvent.memberLeave().getOrganization();

        VacationConfirmMessageForm vacationConfirmMessageForm = VacationConfirmMessageForm.create(
                createdEvent.requesterId(),
                organization.getCompanyId(),
                organization.getDepartmentId(),
                createdEvent.vacationDate(),
                vacation.getId());

        Map<String, Object> vacationConfirmMessageBody = MessageBodyBuilder.from(vacationConfirmMessageForm);
        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.APPROVAL)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .body(vacationConfirmMessageBody)
                .build();
        return messageQ;
    }

    @Async("${event.executor.name}")
    @Transactional(propagation = Propagation.REQUIRED)
    @EventListener(VacationCreatedEvent.class)
    public void listen(CommonVacationCreateEvent createdEvent) {
        try {
            MessageQ messageQ = createMessage(createdEvent);
            messageQRepository.save(messageQ);
        } catch (Exception e) {
            log.info("Fail Create MessageQ createdEvent {}", createdEvent);
            log.error("Error : ", e);
        }
    }

    private static MessageQ createMessage(CommonVacationCreateEvent createdEvent) {
        VacationConfirmMessageForm vacationConfirmMessageForm = VacationConfirmMessageForm.create(
                createdEvent.requesterId(),
                createdEvent.companyId(),
                createdEvent.departmentId(),
                createdEvent.vacationDate(),
                createdEvent.vacationId());

        Map<String, Object> vacationConfirmMessageBody = MessageBodyBuilder.from(vacationConfirmMessageForm);
        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.APPROVAL)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .body(vacationConfirmMessageBody)
                .build();
        return messageQ;
    }
}
