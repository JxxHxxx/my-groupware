package com.jxx.vacation.messaging.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmContentModel;
import com.jxx.vacation.core.message.domain.MessageProcessStatus;
import com.jxx.vacation.core.message.domain.MessageQ;
import com.jxx.vacation.core.message.domain.MessageQResult;
import com.jxx.vacation.core.message.infra.MessageQRepository;
import com.jxx.vacation.core.message.infra.MessageQResultRepository;
import com.jxx.vacation.messaging.infra.ConfirmDocumentRepository;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


import javax.sql.DataSource;
import java.time.LocalDateTime;

import static com.jxx.vacation.core.message.MessageConst.RETRY_HEADER;
import static com.jxx.vacation.core.message.domain.MessageProcessStatus.*;

@Slf4j
@Service(value = "vacationMessageService")
public class VacationMessageService implements MessageService<MessageQ>{

    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;
    private final TransactionTemplate transactionTemplate;
    private final PlatformTransactionManager platformTransactionManager;

    public VacationMessageService(ConfirmDocumentRepository confirmDocumentRepository, MessageQRepository messageQRepository,
                                  MessageQResultRepository messageQResultRepository, TransactionTemplate transactionTemplate,
                                  @Qualifier("approvalDataSource") DataSource dataSource) {
        this.confirmDocumentRepository = confirmDocumentRepository;
        this.messageQRepository = messageQRepository;
        this.messageQResultRepository = messageQResultRepository;
        this.transactionTemplate = transactionTemplate;
        this.platformTransactionManager = new DataSourceTransactionManager(dataSource);
    }
    /**
     * 트랜잭션 관리를 위한 테스트 메서드
     * @param message
     */

    @Override
    public void process(Message<MessageQ> message) {
        MessageQ messageQ = message.getPayload();
        MessageProcessStatus messageProcessStatus = messageQ.getMessageProcessStatus();
        MessageProcessStatus sentMessageProcessStatus = messageProcessStatus;

        TransactionStatus txStatus = platformTransactionManager.getTransaction(TransactionDefinition.withDefaults());

        try {
            switch (messageQ.getMessageDestination()) {
                case CONFIRM -> {
                    VacationConfirmContentModel updateForm = VacationConfirmContentModel.from(messageQ.getBody());
                    confirmDocumentRepository.updateContent(updateForm);
                    sentMessageProcessStatus = SUCCESS;
                    platformTransactionManager.commit(txStatus);
                }
                case APPROVAL -> {
                    VacationConfirmModel confirm = VacationConfirmModel.from(messageQ.getBody());
                    VacationConfirmContentModel confirmContent = VacationConfirmContentModel.from(messageQ.getBody());
                    Long contentPk = confirmDocumentRepository.insertContent(confirmContent);
                    confirmDocumentRepository.insert(contentPk, confirm);
                    sentMessageProcessStatus = SUCCESS;
                    platformTransactionManager.commit(txStatus);
                }
            }
        } catch (RuntimeException e) {
            txStatus.setRollbackOnly();
            sentMessageProcessStatus = FAIL;
            log.error("메시지 변환 중 에러가 발생했습니다. 롤백합니다.", e);
        } catch (JsonProcessingException e) {
            txStatus.setRollbackOnly();
            sentMessageProcessStatus = FAIL;
            log.error("VacationConfirmContentModel 메시지 파싱 중 에러가 발생했습니다. 롤백합니다.", e);
        } finally {
            messageQRepository.deleteById(messageQ.getPk());
            MessageQResult messageQResult = createSentMessageQResult(messageQ, sentMessageProcessStatus);
            messageQResultRepository.save(messageQResult);
        }
    }
    @Override
    public void retry(Message<MessageQ> message) {
        MessageQ messageQ = message.getPayload();
        MessageProcessStatus messageProcessStatus = messageQ.getMessageProcessStatus();

        TransactionStatus txStatus = platformTransactionManager.getTransaction(TransactionDefinition.withDefaults());

        MessageProcessStatus retryMessageProcessStatus = messageProcessStatus;
        Long originalMessagePk = null;
        try {
            VacationConfirmModel confirm = VacationConfirmModel.from(messageQ.getBody());
            VacationConfirmContentModel confirmContent = VacationConfirmContentModel.from(messageQ.getBody());
            Long contentPk = confirmDocumentRepository.insertContent(confirmContent);
            confirmDocumentRepository.insert(contentPk, confirm);
            retryMessageProcessStatus = SUCCESS;
            originalMessagePk = message.getHeaders().get(RETRY_HEADER, Long.class);
            platformTransactionManager.commit(txStatus);

        } catch (Exception e) {
            txStatus.setRollbackOnly();
            log.warn("메시지 변환 중 오류가 발생했습니다.", e);
            retryMessageProcessStatus = FAIL;
            // 아래 if 분기 왜 만들었는지 확인 파악
            if (!message.getHeaders().containsKey(RETRY_HEADER)) {
                originalMessagePk = MessageQ.ERROR_ORIGINAL_MESSAGE_PK;
            }
            originalMessagePk = message.getHeaders().get(RETRY_HEADER, Long.class);
        }
        finally {
            messageQRepository.deleteById(messageQ.getPk());
            MessageQResult messageQResult = createRetryMessageQResult(messageQ, retryMessageProcessStatus, originalMessagePk);
            messageQResultRepository.save(messageQResult);
            txStatus.flush();
        }
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
