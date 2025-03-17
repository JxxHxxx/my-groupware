package com.jxx.groupware.messaging.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.groupware.core.messaging.body.vendor.confirm.VacationConfirmContentModel;
import com.jxx.groupware.core.messaging.body.vendor.confirm.VacationConfirmUpdateContentModel;
import com.jxx.groupware.core.messaging.domain.MessageProcessStatus;
import com.jxx.groupware.core.messaging.domain.MessageQ;
import com.jxx.groupware.core.messaging.domain.MessageQResult;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.messaging.infra.MessageQResultRepository;
import com.jxx.groupware.messaging.infra.mapper.ConfirmDocumentMapper;
import com.jxx.groupware.messaging.infra.ConfirmDocumentRepository;
import com.jxx.groupware.core.messaging.body.vendor.confirm.VacationConfirmModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static com.jxx.groupware.core.messaging.MessageConst.RETRY_HEADER;
import static com.jxx.groupware.core.messaging.domain.MessageProcessStatus.*;

@Slf4j
@Service(value = "vacationMessageService")
public class VacationMessageService implements MessageService<MessageQ>{

    private final ConfirmDocumentRepository confirmDocumentRepository;
    private final ConfirmDocumentMapper confirmDocumentMapper;
    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public VacationMessageService(ConfirmDocumentRepository confirmDocumentRepository,
                                  ConfirmDocumentMapper confirmDocumentMapper,
                                  MessageQRepository messageQRepository,
                                  MessageQResultRepository messageQResultRepository,
                                  @Qualifier("approvalDataSource") DataSource dataSource) {
        this.confirmDocumentRepository = confirmDocumentRepository;
        this.confirmDocumentMapper = confirmDocumentMapper;
        this.messageQRepository = messageQRepository;
        this.messageQResultRepository = messageQResultRepository;
        // JDBC 용 트랜잭션 매니저
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

        // 비즈니스 로직 START
        try {
            switch (messageQ.getMessageProcessType()) {
                case UPDATE -> {
                    VacationConfirmUpdateContentModel updateForm = VacationConfirmUpdateContentModel.from(messageQ.getBody());
                    confirmDocumentMapper.updateContent(updateForm);
                    confirmDocumentRepository.updateVacationDuration(updateForm);
                }
                case INSERT -> {
                    VacationConfirmModel confirm = VacationConfirmModel.from(messageQ.getBody());
                    VacationConfirmContentModel confirmContent = VacationConfirmContentModel.from(messageQ.getBody());
                    Long contentPk = confirmDocumentRepository.insertContent(confirmContent);
                    confirmDocumentRepository.insert(contentPk, confirm);
                }
            }
            sentMessageProcessStatus = SUCCESS;
            platformTransactionManager.commit(txStatus);
        } catch (RuntimeException e) {
            txStatus.setRollbackOnly();
            sentMessageProcessStatus = FAIL;
            log.error("메시지 변환 중 에러가 발생했습니다. 롤백합니다.", e);
        } catch (JsonProcessingException e) {
            txStatus.setRollbackOnly();
            sentMessageProcessStatus = FAIL;
            log.error("VacationConfirmContentModel 메시지 파싱 중 에러가 발생했습니다. 롤백합니다.", e);
        } finally {
            // JpaTransactionManager 가 트랜잭션을 관리하기 때문에 위 롤백에 영향없음
            messageQRepository.deleteById(messageQ.getPk());
            MessageQResult messageQResult = createSentMessageQResult(messageQ, sentMessageProcessStatus);
            messageQResultRepository.save(messageQResult);
        }
        // 비즈니스 로직 END
    }
    @Override
    public void retry(Message<MessageQ> message) {
        MessageQ messageQ = message.getPayload();
        MessageProcessStatus messageProcessStatus = messageQ.getMessageProcessStatus();
        MessageProcessStatus retryMessageProcessStatus = messageProcessStatus;

        TransactionStatus txStatus = platformTransactionManager.getTransaction(TransactionDefinition.withDefaults());

        Long originalMessagePk = null;
        try {
            switch (messageQ.getMessageProcessType()) {
                case UPDATE -> {
                    VacationConfirmUpdateContentModel updateForm = VacationConfirmUpdateContentModel.from(messageQ.getBody());
                    confirmDocumentMapper.updateContent(updateForm);
                    confirmDocumentRepository.updateVacationDuration(updateForm);
                }
                case INSERT -> {
                    VacationConfirmModel confirm = VacationConfirmModel.from(messageQ.getBody());
                    VacationConfirmContentModel confirmContent = VacationConfirmContentModel.from(messageQ.getBody());
                    Long contentPk = confirmDocumentRepository.insertContent(confirmContent);
                    confirmDocumentRepository.insert(contentPk, confirm);
                }
            }
            retryMessageProcessStatus = SUCCESS;
            originalMessagePk = message.getHeaders().get(RETRY_HEADER, Long.class);
            platformTransactionManager.commit(txStatus);

        } catch (Exception e) {
            txStatus.setRollbackOnly();
            log.warn("메시지 변환 중 오류가 발생했습니다.", e);
            retryMessageProcessStatus = FAIL;
            // RETRY 키에 재시도 해더가 들어가는데
            if (!message.getHeaders().containsKey(RETRY_HEADER)) {
                originalMessagePk = MessageQ.ERROR_ORIGINAL_MESSAGE_PK;
            }
            originalMessagePk = message.getHeaders().get(RETRY_HEADER, Long.class);
        }
        finally {
            // JpaTransactionManager 가 트랜잭션을 관리하기 때문에 위 롤백에 영향없음
            messageQRepository.deleteById(messageQ.getPk());
            MessageQResult messageQResult = createRetryMessageQResult(messageQ, retryMessageProcessStatus, originalMessagePk);
            messageQResultRepository.save(messageQResult);
            txStatus.flush();
        }
    }

    private static MessageQResult createSentMessageQResult(
            MessageQ messageQ,
            MessageProcessStatus messageProcessStatus) {
        return MessageQResult.builder()
                .messageProcessStatus(messageProcessStatus)
                .processStartTime(messageQ.getProcessStartTime())
                .processEndTime(LocalDateTime.now())
                .messageProcessType(messageQ.getMessageProcessType())
                .messageDestination(messageQ.getMessageDestination())
                .eventTime(messageQ.getEventTime())
                .originalMessagePk(messageQ.getPk())
                .body(messageQ.getBody())
                .build();
    }

    private static MessageQResult createRetryMessageQResult(
            MessageQ messageQ,
            MessageProcessStatus retryMessageProcessStatus,
            Long originalMessagePk) {
        return MessageQResult.builder()
                .messageProcessStatus(retryMessageProcessStatus)
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
