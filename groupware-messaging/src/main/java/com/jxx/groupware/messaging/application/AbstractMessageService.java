package com.jxx.groupware.messaging.application;

import com.jxx.groupware.core.messaging.domain.NonUniqueWriteException;
import com.jxx.groupware.core.messaging.domain.UnProcessableException;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.core.messaging.domain.queue.MessageQResult;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.messaging.infra.MessageQResultRepository;
import com.jxx.groupware.messaging.application.sql.builder.RdbMessagePolicyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.jxx.groupware.core.messaging.MessageConst.RETRY_HEADER;
import static com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus.*;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageService implements MessageService<MessageQ> {
    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;

    /**
     * MessageService 구현체의 공통 로직을 아래 담는다.
     * 구현체 별 상이한 로직은 processMessage 에서 처리한다.
     **/

    @Override
    public void process(Message<MessageQ> message) {
        log.debug("call abstractMessageService process");
        log.info("txName : {}", TransactionSynchronizationManager.getCurrentTransactionName());
        MessageQ payload = message.getPayload();
        MessageProcessStatus messageProcessStatus = payload.getMessageProcessStatus();
        MessageProcessStatus sentMessageProcessStatus = messageProcessStatus;
        try {
            processMessage(payload);
            sentMessageProcessStatus = SUCCESS;
        } catch (UnProcessableException exception) {
            sentMessageProcessStatus = UNPROCESSABLE;
            log.error("해당 메시지는 처리가 불가능합니다. 롤백 및 메시지 상태를 UNPROCESSABLE으로 변경합니다.", exception);
        } catch (NonUniqueWriteException exception) {
            log.error("{} 롤백 및 메시지 상태를 FAIL로 변경합니다.", exception.getMessage(), exception);
            sentMessageProcessStatus = FAIL;
        } catch (RdbMessagePolicyException exception) {
            log.error("{}", exception.getMessage() + " 롤백 및 메시지 상태를 FAIL로 변경합니다.", exception);
            sentMessageProcessStatus = FAIL;
        } catch (RuntimeException exception) {
            log.error("메시지 변환 중 에러가 발생했습니다. 롤백 및 메시지 상태를 FAIL로 변경합니다.", exception);
            sentMessageProcessStatus = FAIL;
        } catch (Exception exception) {
            log.error("메시지 변환 중 에러가 발생했습니다. 롤백 및 메시지 상태를 FAIL로 변경합니다.", exception);
            sentMessageProcessStatus = FAIL;
        } finally {
            messageQRepository.deleteById(payload.getPk());
            MessageQResult messageQResult = createSentMessageQResult(message, sentMessageProcessStatus);
            messageQResultRepository.save(messageQResult);
        }
    }


    @Override
    public void retry(Message<MessageQ> message) {
        log.debug("call abstractMessageService retry");
        MessageQ payload = message.getPayload();
        MessageProcessStatus messageProcessStatus = payload.getMessageProcessStatus();
        MessageProcessStatus sentMessageProcessStatus = messageProcessStatus;
        try {
            processMessage(payload);
            sentMessageProcessStatus = SUCCESS;
        } catch (UnProcessableException exception) {
            sentMessageProcessStatus = UNPROCESSABLE;
            log.error("해당 메시지는 처리가 불가능합니다. 롤백 및 메시지 상태를 UNPROCESSABLE으로 변경합니다.", exception);
        } catch (NonUniqueWriteException exception) {
            log.error("레코드 2개 이상에 쓰기 작업이 이루어졌습니다. RDB 메시징 서비스 정책에 위배됩니다. 롤백합니다.", exception);
            sentMessageProcessStatus = FAIL;
        } catch (RuntimeException exception) {
            log.error("메시지 변환 중 에러가 발생했습니다. 롤백 및 메시지 상태를 FAIL로 변경합니다.", exception);
            sentMessageProcessStatus = FAIL;
        } catch (Exception exception) {
            sentMessageProcessStatus = FAIL;
            log.error("메시지 변환 중 에러가 발생했습니다. 롤백 및 메시지 상태를 FAIL로 변경합니다.", exception);
        } finally {
            messageQRepository.deleteById(payload.getPk());

            MessageQResult messageQResult = createSentMessageQResult(message, sentMessageProcessStatus);
            messageQResultRepository.save(messageQResult);
        }
    }

    /**
     * MessageService 실 구현체에서 MessageQ를 처리해야하는 로직 부분
     **/
    protected abstract void processMessage(MessageQ message);

    private MessageQResult createSentMessageQResult(Message<MessageQ> message, MessageProcessStatus messageProcessStatus) {
        Long retryId = message.getHeaders().get(RETRY_HEADER, Long.class);
        MessageQ messageQ = message.getPayload();
        // 최초 생산 / 재시도 구분 처리
        Long originalMessagePk = Objects.isNull(retryId) ? messageQ.getPk() : retryId;

        return MessageQResult.builder()
                .messageProcessStatus(messageProcessStatus)
                .processStartTime(messageQ.getProcessStartTime())
                .processEndTime(LocalDateTime.now())
                .messageProcessType(messageQ.getMessageProcessType())
                .messageDestination(messageQ.getMessageDestination())
                .eventTime(messageQ.getEventTime())
                .originalMessagePk(originalMessagePk)
                .body(messageQ.getBody())
                .build();
    }
}
