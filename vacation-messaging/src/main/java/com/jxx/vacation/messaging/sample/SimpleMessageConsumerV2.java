package com.jxx.vacation.messaging.sample;

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

import static com.jxx.vacation.core.message.MessageConst.RETRY_HEADER;
import static com.jxx.vacation.core.message.MessageProcessStatus.FAIL;
import static com.jxx.vacation.core.message.MessageProcessStatus.SUCCESS;

@Slf4j
@MessageEndpoint
@RequiredArgsConstructor
public class SimpleMessageConsumerV2 {

    @Qualifier("approvalNamedParameterJdbcTemplate")
    private final ApprovalRepository approvalRepository;
    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;

    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_1(List<Message<MessageQ>> message) {processes(message, "1_1");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_2(List<Message<MessageQ>> message) {processes(message, "1_2");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_3(List<Message<MessageQ>> message) {processes(message, "1_3");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_5(List<Message<MessageQ>> message) {processes(message, "1_5");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_6(List<Message<MessageQ>> message) {processes(message, "1_6");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_7(List<Message<MessageQ>> message) {processes(message, "1_7");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_8(List<Message<MessageQ>> message) {processes(message, "1_8");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_9(List<Message<MessageQ>> message) {processes(message, "1_9");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1", async = "true")
    public void consumeSentMessage1_10(List<Message<MessageQ>> message) {processes(message, "1_10");}


    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_1(List<Message<MessageQ>> message) {processes(message, "2_1");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_2(List<Message<MessageQ>> message) {processes(message, "2_2");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_3(List<Message<MessageQ>> message) {processes(message, "2_3");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_5(List<Message<MessageQ>> message) {processes(message, "2_5");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_6(List<Message<MessageQ>> message) {processes(message, "2_6");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_7(List<Message<MessageQ>> message) {processes(message, "2_7");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_8(List<Message<MessageQ>> message) {processes(message, "2_8");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_9(List<Message<MessageQ>> message) {processes(message, "2_9");}
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2", async = "true")
    public void consumeSentMessage2_10(List<Message<MessageQ>> message) {processes(message, "2_10");}

    private void processes(List<Message<MessageQ>> messages, String channelNum) {
        for (Message<MessageQ> message : messages) {
            log.info("=================START====================\n" +
                    "Channel:sentQueueChannel{}\n" +
                    "Message:{}", channelNum, message);
            MessageQ messageQ = message.getPayload();
            MessageProcessStatus sentMessageProcessStatus = null;
            try {
                VacationConfirmModel vacationConfirmModel = createVacationConfirmModel(messageQ.getBody());
                approvalRepository.insert(vacationConfirmModel); // 비즈니스 로직
                sentMessageProcessStatus = SUCCESS;
            } catch (Exception e) {
                log.warn("메시지 변환 중 오류가 발생했습니다.", e);
                sentMessageProcessStatus = FAIL;
            } finally {
                messageQRepository.deleteById(messageQ.getPk());
                MessageQResult messageQResult = createSentMessageQResult(messageQ, sentMessageProcessStatus);
                messageQResultRepository.save(messageQResult);
            }
            log.info("================= END ====================");
        }
    }



    private void process(Message<MessageQ> message, String channelNum) {

        log.info("=================START====================\n" +
                "Channel:sentQueueChannel{}\n" +
                "Message:{}", channelNum, message);
        MessageQ messageQ = message.getPayload();
        MessageProcessStatus sentMessageProcessStatus = null;
        try {
            VacationConfirmModel vacationConfirmModel = createVacationConfirmModel(messageQ.getBody());
            approvalRepository.insert(vacationConfirmModel); // 비즈니스 로직
            sentMessageProcessStatus = SUCCESS;
        } catch (Exception e) {
            log.warn("메시지 변환 중 오류가 발생했습니다.", e);
            sentMessageProcessStatus = FAIL;
        } finally {
            messageQRepository.deleteById(messageQ.getPk());
            MessageQResult messageQResult = createSentMessageQResult(messageQ, sentMessageProcessStatus);
            messageQResultRepository.save(messageQResult);
        }
        log.info("================= END ====================");
    }

    @Transactional
    @ServiceActivator(inputChannel = "retryQueueChannel")
    public void consumeRetryMessage(Message<MessageQ> message) {
        log.info("=================START====================\n" +
                "Channel:retryQueueChannel\n" +
                "Message:{}", message);
        MessageQ messageQ = message.getPayload();
        MessageProcessStatus retryMessageProcessStatus = null;
        Long originalMessagePk = null;
        try {
            VacationConfirmModel vacationConfirmModel = createVacationConfirmModel(messageQ.getBody());
            approvalRepository.insert(vacationConfirmModel); // 비즈니스 로직
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
        log.info("================= END ====================");
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

    private static VacationConfirmModel createVacationConfirmModel(Map<String, Object> body) {
        String confirmStatus = (String) body.get("confirm_status");
        String confirmDocumentId = String.valueOf(body.get("confirm_document_id"));
        String createSystem = (String) body.get("create_system");
        String documentType = (String) body.get("document_type");
        String companyId = (String) body.get("company_id");
        String departmentId = (String) body.get("department_id");
        String requesterId = (String) body.get("requester_id");
        LocalDateTime createTime = convertToCreateTime(body);

        return new VacationConfirmModel(confirmStatus, confirmDocumentId, createSystem, createTime, documentType, companyId, departmentId, requesterId);
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
