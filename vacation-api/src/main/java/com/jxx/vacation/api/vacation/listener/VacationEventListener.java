package com.jxx.vacation.api.vacation.listener;


import com.jxx.vacation.core.message.*;
import com.jxx.vacation.core.message.payload.approval.form.VacationApprovalForm;
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

import static com.jxx.vacation.core.message.payload.approval.DocumentType.VAC;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacationEventListener {

    private static final String CREATE_SYSTEM = "JXX-API-APP";
    private static final String APPROVAL_LINE_LIFECYCLE_INITIAL_VALUE = "BEFORE_CREATE";
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

        VacationApprovalForm vacationApprovalForm = VacationApprovalForm.create(
                createdEvent.requesterId(),
                organization.getCompanyId(),
                organization.getDepartmentId(),
                CREATE_SYSTEM,
                VAC,
                createdEvent.vacationDate(),
                vacation.getId(),
                APPROVAL_LINE_LIFECYCLE_INITIAL_VALUE
                );

        Map<String, Object> messageBody = MessageBodyBuilder.createVacationApprovalBody(vacationApprovalForm);
        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.APPROVAL)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .body(messageBody)
                .build();
        return messageQ;
    }
}
