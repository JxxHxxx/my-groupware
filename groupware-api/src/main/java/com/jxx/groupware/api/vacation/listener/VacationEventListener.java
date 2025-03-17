package com.jxx.groupware.api.vacation.listener;


import com.jxx.groupware.core.messaging.*;
import com.jxx.groupware.core.messaging.body.vendor.confirm.*;
import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessType;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.domain.entity.Organization;
import com.jxx.groupware.core.vacation.domain.entity.Vacation;
import com.jxx.groupware.core.vacation.domain.entity.VacationDuration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacationEventListener {

    private final MessageQRepository messageQRepository;

    // 휴가 생성 시 메시지 발행
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
    // 공동 연차 생성 시 메시지 발행
    @Async("${event.executor.name}")
    @Transactional(propagation = Propagation.REQUIRED)
    @EventListener(CommonVacationCreateEvent.class)
    public void listen(CommonVacationCreateEvent createdEvent) {
        try {
            MessageQ messageQ = createMessage(createdEvent);
            messageQRepository.save(messageQ);
        } catch (Exception e) {
            log.info("Fail create MessageQ createdEvent {}", createdEvent);
            log.error("Error : ", e);
        }
    }

    @Async("${event.executor.name}")
    @Transactional(propagation = Propagation.REQUIRED)
    @EventListener(VacationUpdatedEvent.class)
    public void listen(VacationUpdatedEvent updatedEvent) {
        try {
            MessageQ messageQ = updateMessage(updatedEvent);
            messageQRepository.save(messageQ);
        } catch (Exception e) {
            log.info("Fail create MessageQ updatedEvent {}", updatedEvent);
            log.error("Error : ", e);
        }
    }
    private MessageQ updateMessage(VacationUpdatedEvent updatedEvent) {
        Vacation vacation = updatedEvent.vacation();
        List<VacationDuration> vacationDurationEntities = vacation.getVacationDurations();
        List<VacationDurationModel> vacationDurationModel = vacationDurationEntities.stream()
                .map(vd -> new VacationDurationModel(String.valueOf(vd.getStartDateTime()), String.valueOf(vd.getEndDateTime())))
                .toList();

        VacationUpdateMessageForm vacationUpdateMessageForm = new VacationUpdateMessageForm(
                vacation.getId(),
                DocumentType.VAC,
                vacation.getCompanyId(),
                updatedEvent.delegatorId(),
                updatedEvent.delegatorName(),
                updatedEvent.reason(),
                vacationDurationModel,
                updatedEvent.contentPk()
        );
        Map<String, Object> body = MessageBodyBuilder.from(vacationUpdateMessageForm);
        return MessageQ.builder()
                .messageDestination(MessageDestination.CONFIRM)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .messageProcessType(MessageProcessType.UPDATE)
                .body(body)
                .build();
    }

    private static MessageQ createMessage(VacationCreatedEvent createdEvent) {
        Vacation vacation = createdEvent.vacation();
        List<VacationDuration> vacationDurationEntities = vacation.getVacationDurations();

        List<VacationDurationModel> vacationDurations = vacationDurationEntities.stream()
                .map(vd -> new VacationDurationModel(String.valueOf(vd.getStartDateTime()), String.valueOf(vd.getEndDateTime())))
                .toList();

        MemberLeave memberLeave = createdEvent.memberLeave();
        Organization organization = memberLeave.getOrganization();

        VacationConfirmMessageForm messageForm = VacationConfirmMessageForm.create(
                vacation.getRequesterId(),
                organization.getCompanyId(),
                organization.getDepartmentId(),
                createdEvent.vacationDate(),
                vacation.getId(),
                createdEvent.title(),
                createdEvent.delegatorId(),
                createdEvent.delegatorName(),
                createdEvent.reason(),
                memberLeave.getName(),
                organization.getDepartmentName(),
                vacationDurations);

        Map<String, Object> vacationConfirmMessageBody = MessageBodyBuilder.from(messageForm);
        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.CONFIRM)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .messageProcessType(MessageProcessType.INSERT)
                .body(vacationConfirmMessageBody)
                .build();
        return messageQ;
    }

    private static MessageQ createMessage(CommonVacationCreateEvent createdEvent) {
        Vacation vacation = createdEvent.vacation();
        List<VacationDurationModel> vacationDurationModels = vacation.getVacationDurations().stream()
                .map(vd -> new VacationDurationModel(String.valueOf(vd.getStartDateTime()), String.valueOf(vd.getEndDateTime())))
                .toList();
        CommonVacationConfirmMessageForm messageForm = CommonVacationConfirmMessageForm.create(
                vacation.getRequesterId(),
                vacation.getCompanyId(),
                createdEvent.departmentId(),
                createdEvent.vacationDate(),
                vacation.getId(),
                createdEvent.requesterName(),
                createdEvent.departmentName(),
                vacationDurationModels);

        Map<String, Object> vacationConfirmMessageBody = MessageBodyBuilder.from(messageForm);
        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.CONFIRM)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .messageProcessType(MessageProcessType.INSERT)
                .body(vacationConfirmMessageBody)
                .build();
        return messageQ;
    }
}
