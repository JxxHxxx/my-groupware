package com.jxx.vacation.messaging.application;

import com.jxx.vacation.core.message.domain.MessageProcessStatus;
import com.jxx.vacation.core.message.domain.MessageQ;
import com.jxx.vacation.core.message.domain.MessageQResult;
import com.jxx.vacation.core.message.infra.MessageQRepository;
import com.jxx.vacation.core.message.infra.MessageQResultRepository;
import com.jxx.vacation.messaging.infra.ApprovalRepository;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.jxx.vacation.core.message.MessageConst.RETRY_HEADER;
import static com.jxx.vacation.core.message.domain.MessageProcessStatus.FAIL;
import static com.jxx.vacation.core.message.domain.MessageProcessStatus.SUCCESS;

@Slf4j
@Service(value = "vacationMessageService")
@RequiredArgsConstructor
public class VacationMessageService implements MessageService<MessageQ>{

    @Qualifier("approvalNamedParameterJdbcTemplate")
    private final ApprovalRepository approvalRepository;
    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;

    @Override
    public void process(Message<MessageQ> message) {
        MessageQ messageQ = message.getPayload();
        MessageProcessStatus sentMessageProcessStatus = null;
        try {
            VacationConfirmModel vacationConfirmModel = VacationConfirmModel.from(messageQ.getBody());
            approvalRepository.insert(vacationConfirmModel);
            sentMessageProcessStatus = SUCCESS;
        } catch (Exception e) {
            log.warn("메시지 변환 중 오류가 발생했습니다.", e);
            sentMessageProcessStatus = FAIL;
        } finally {
            messageQRepository.deleteById(messageQ.getPk());
            MessageQResult messageQResult = createSentMessageQResult(messageQ, sentMessageProcessStatus);
            messageQResultRepository.save(messageQResult);
        }
    }

    @Override
    public void retry(Message<MessageQ> message) {
        MessageQ messageQ = message.getPayload();
        MessageProcessStatus retryMessageProcessStatus = null;
        Long originalMessagePk = null;
        try {
            VacationConfirmModel vacationConfirmModel = VacationConfirmModel.from(messageQ.getBody());
            approvalRepository.insert(vacationConfirmModel);
            retryMessageProcessStatus = SUCCESS;
            originalMessagePk = message.getHeaders().get(RETRY_HEADER, Long.class);

        } catch (Exception e) {
            log.warn("메시지 변환 중 오류가 발생했습니다.", e);
            retryMessageProcessStatus = FAIL;
            if (!message.getHeaders().containsKey(RETRY_HEADER)) {
                originalMessagePk = MessageQ.ERROR_ORIGINAL_MESSAGE_PK;
            }
            originalMessagePk = message.getHeaders().get(RETRY_HEADER, Long.class);
        }
        finally {
            messageQRepository.deleteById(messageQ.getPk());
            MessageQResult messageQResult = createRetryMessageQResult(messageQ, retryMessageProcessStatus, originalMessagePk);
            messageQResultRepository.save(messageQResult);
        }
//            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> HISTORY START >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//            log.info("Channel:sentQueueChannel{}", channelNum);
//            log.info("Message:{}", message);
//            log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< HISTORY  END  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    private static MessageQResult createSentMessageQResult(MessageQ messageQ, MessageProcessStatus messageProcessStatus) {
        return MessageQResult.builder()
                .messageProcessStatus(messageProcessStatus)
                .processStartTime(messageQ.getProcessStartTime())
                .processEndTime(LocalDateTime.now())
                .messageDestination(messageQ.getMessageDestination())
                .eventTime(messageQ.getEventTime())
                .originalMessagePk(messageQ.getPk())
                .body(messageQ.getBody())
                .build();
    }

    private static MessageQResult createRetryMessageQResult(MessageQ messageQ, MessageProcessStatus retryMessageProcessStatus, Long originalMessagePk) {
        return MessageQResult.builder()
                .messageProcessStatus(retryMessageProcessStatus)
                .processStartTime(messageQ.getProcessStartTime())
                .processEndTime(LocalDateTime.now())
                .messageDestination(messageQ.getMessageDestination())
                .eventTime(messageQ.getEventTime())
                .originalMessagePk(originalMessagePk)
                .body(messageQ.getBody())
                .build();
    }
}
