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

import static com.jxx.vacation.core.message.MessageConst.RETRY_HEADER;
import static com.jxx.vacation.core.message.MessageProcessStatus.*;

@Slf4j
//@MessageEndpoint
@RequiredArgsConstructor
public class SimpleMessageConsumer {

    @Qualifier("approvalNamedParameterJdbcTemplate")
    private final ApprovalRepository approvalRepository;
    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;

    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1")
    public void consumeSentMessage1_1(Message<MessageQ> message) {
        process(message, "1_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2")
    public void consumeSentMessage2_1(Message<MessageQ> message) {
        process(message, "2_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel3")
    public void consumeSentMessage3_1(Message<MessageQ> message) {process(message, "3_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel4")
    public void consumeSentMessage4_1(Message<MessageQ> message) {
        process(message, "4_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel5")
    public void consumeSentMessage5_1(Message<MessageQ> message) {
        process(message, "5_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel6")
    public void consumeSentMessage6_1(Message<MessageQ> message) {
        process(message, "6_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel7")
    public void consumeSentMessage7_1(Message<MessageQ> message) {
        process(message, "7_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel8")
    public void consumeSentMessage8_1(Message<MessageQ> message) {process(message, "8_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel9")
    public void consumeSentMessage9_1(Message<MessageQ> message) {
        process(message, "9_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel10")
    public void consumeSentMessage10_1(Message<MessageQ> message) {
        process(message, "10_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel11")
    public void consumeSentMessage11_1(Message<MessageQ> message) {
        process(message, "11_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel12")
    public void consumeSentMessage12_1(Message<MessageQ> message) {
        process(message, "12_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel13")
    public void consumeSentMessage13_1(Message<MessageQ> message) {process(message, "13_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel14")
    public void consumeSentMessage14_1(Message<MessageQ> message) {
        process(message, "14_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel15")
    public void consumeSentMessage15_1(Message<MessageQ> message) {
        process(message, "15_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel16")
    public void consumeSentMessage16_1(Message<MessageQ> message) {
        process(message, "16_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel17")
    public void consumeSentMessage17_1(Message<MessageQ> message) {
        process(message, "17_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel18")
    public void consumeSentMessage18_1(Message<MessageQ> message) {process(message, "18_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel19")
    public void consumeSentMessage19_1(Message<MessageQ> message) {
        process(message, "19_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel20")
    public void consumeSentMessage20_1(Message<MessageQ> message) {
        process(message, "20_1");
    }

    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel21")
    public void consumeSentMessage21_1(Message<MessageQ> message) {
        process(message, "21_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel22")
    public void consumeSentMessage22_1(Message<MessageQ> message) {
        process(message, "22_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel23")
    public void consumeSentMessage23_1(Message<MessageQ> message) {process(message, "23_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel24")
    public void consumeSentMessage24_1(Message<MessageQ> message) {
        process(message, "24_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel25")
    public void consumeSentMessage25_1(Message<MessageQ> message) {
        process(message, "25_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel26")
    public void consumeSentMessage26_1(Message<MessageQ> message) {
        process(message, "26_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel27")
    public void consumeSentMessage27_1(Message<MessageQ> message) {
        process(message, "27_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel28")
    public void consumeSentMessage28_1(Message<MessageQ> message) {process(message, "28_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel29")
    public void consumeSentMessage29_1(Message<MessageQ> message) {
        process(message, "29_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel30")
    public void consumeSentMessage30_1(Message<MessageQ> message) {
        process(message, "30_1");
    }

    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel31")
    public void consumeSentMessage31_1(Message<MessageQ> message) {
        process(message, "31_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel32")
    public void consumeSentMessage32_1(Message<MessageQ> message) {
        process(message, "32_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel33")
    public void consumeSentMessage33_1(Message<MessageQ> message) {process(message, "33_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel34")
    public void consumeSentMessage34_1(Message<MessageQ> message) {
        process(message, "34_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel35")
    public void consumeSentMessage35_1(Message<MessageQ> message) {
        process(message, "35_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel36")
    public void consumeSentMessage36_1(Message<MessageQ> message) {
        process(message, "36_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel37")
    public void consumeSentMessage37_1(Message<MessageQ> message) {
        process(message, "37_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel38")
    public void consumeSentMessage38_1(Message<MessageQ> message) {process(message, "38_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel39")
    public void consumeSentMessage39_1(Message<MessageQ> message) {
        process(message, "39_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel40")
    public void consumeSentMessage40_1(Message<MessageQ> message) {
        process(message, "40_1");
    }

    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel41")
    public void consumeSentMessage41_1(Message<MessageQ> message) {
        process(message, "41_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel42")
    public void consumeSentMessage42_1(Message<MessageQ> message) {
        process(message, "42_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel43")
    public void consumeSentMessage43_1(Message<MessageQ> message) {process(message, "43_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel44")
    public void consumeSentMessage44_1(Message<MessageQ> message) {
        process(message, "44_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel45")
    public void consumeSentMessage45_1(Message<MessageQ> message) {
        process(message, "45_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel46")
    public void consumeSentMessage46_1(Message<MessageQ> message) {
        process(message, "46_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel47")
    public void consumeSentMessage47_1(Message<MessageQ> message) {
        process(message, "47_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel48")
    public void consumeSentMessage48_1(Message<MessageQ> message) {process(message, "48_1"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel49")
    public void consumeSentMessage49_1(Message<MessageQ> message) {
        process(message, "49_1");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel50")
    public void consumeSentMessage50_1(Message<MessageQ> message) {
        process(message, "50_1");
    }

    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel1")
    public void consumeSentMessage1_2(Message<MessageQ> message) {
        process(message, "1_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel2")
    public void consumeSentMessage2_2(Message<MessageQ> message) {
        process(message, "2_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel3")
    public void consumeSentMessage3_2(Message<MessageQ> message) {process(message, "3_2"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel4")
    public void consumeSentMessage4_2(Message<MessageQ> message) {
        process(message, "4_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel5")
    public void consumeSentMessage5_2(Message<MessageQ> message) {
        process(message, "5_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel6")
    public void consumeSentMessage6_2(Message<MessageQ> message) {
        process(message, "6_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel7")
    public void consumeSentMessage7_2(Message<MessageQ> message) {
        process(message, "7_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel8")
    public void consumeSentMessage8_2(Message<MessageQ> message) {process(message, "8_2"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel9")
    public void consumeSentMessage9_2(Message<MessageQ> message) {
        process(message, "9_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel10")
    public void consumeSentMessage10_2(Message<MessageQ> message) {
        process(message, "10_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel11")
    public void consumeSentMessage11_2(Message<MessageQ> message) {
        process(message, "11_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel12")
    public void consumeSentMessage12_2(Message<MessageQ> message) {
        process(message, "12_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel13")
    public void consumeSentMessage13_2(Message<MessageQ> message) {process(message, "13_2"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel14")
    public void consumeSentMessage14_2(Message<MessageQ> message) {
        process(message, "14_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel15")
    public void consumeSentMessage15_2(Message<MessageQ> message) {
        process(message, "15_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel16")
    public void consumeSentMessage16_2(Message<MessageQ> message) {
        process(message, "16_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel17")
    public void consumeSentMessage17_2(Message<MessageQ> message) {
        process(message, "17_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel18")
    public void consumeSentMessage18_2(Message<MessageQ> message) {process(message, "18_2"); }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel19")
    public void consumeSentMessage19_2(Message<MessageQ> message) {
        process(message, "19_2");
    }
    @Transactional
    @ServiceActivator(inputChannel = "sentQueueChannel20")
    public void consumeSentMessage20_2(Message<MessageQ> message) {
        process(message, "20_2");
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
