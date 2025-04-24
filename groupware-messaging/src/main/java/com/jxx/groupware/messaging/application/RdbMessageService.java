package com.jxx.groupware.messaging.application;

import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageTableMapping;
import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.core.messaging.domain.queue.MessageQResult;
import com.jxx.groupware.core.messaging.infra.MessageColumnMappingRepository;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.messaging.infra.MessageQResultRepository;
import com.jxx.groupware.core.messaging.infra.MessageTableMappingRepository;
import com.jxx.groupware.messaging.application.sql.builder.QueryBuilderParameter;
import com.jxx.groupware.messaging.application.sql.builder.SqlQueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus.FAIL;
import static com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus.SUCCESS;


@Slf4j
@Service(value = "rdbMessageService")
public class RdbMessageService implements MessageService<MessageQ> {

    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;
    private final Map<String, DataSource> destinationDataSourceMap;
    private final MessageTableMappingRepository tableMappingRepository;
    private final MessageColumnMappingRepository columnMappingRepository;
    private final SqlQueryBuilder sqlQueryBuilder;

    public RdbMessageService(MessageQRepository messageQRepository,
                             MessageQResultRepository messageQResultRepository,
                             @Qualifier("destinationDataSourceMap") Map<String, DataSource> destinationDataSourceMap,
                             MessageTableMappingRepository tableMappingRepository,
                             MessageColumnMappingRepository columnMappingRepository,
                             SqlQueryBuilder sqlQueryBuilder) {
        this.messageQRepository = messageQRepository;
        this.messageQResultRepository = messageQResultRepository;
        this.destinationDataSourceMap = destinationDataSourceMap;
        this.tableMappingRepository = tableMappingRepository;
        this.columnMappingRepository = columnMappingRepository;
        this.sqlQueryBuilder = sqlQueryBuilder;
    }

    @Override
    public void process(Message<MessageQ> message) {
        processMessage(message);
    }

    @Override
    public void retry(Message<MessageQ> message) {
        processMessage(message);
    }

    private void processMessage(Message<MessageQ> message) {
        MessageQ payload = message.getPayload();
        MessageProcessStatus messageProcessStatus = payload.getMessageProcessStatus();
        MessageProcessStatus sentMessageProcessStatus = messageProcessStatus;

        // DB Bean 선택
        MessageDestination messageDestination = payload.getMessageDestination();

        DataSource dataSource = destinationDataSourceMap.get(messageDestination.name());
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> messageBody = payload.getBody();
        String serviceId = String.valueOf(messageBody.get("SERVICEID"));

        MessageTableMapping tableMapping = tableMappingRepository.findByServiceId(serviceId).orElseThrow();
        String tableName = tableMapping.getTableName();

        List<String> columnNames = columnMappingRepository.findByServiceId(serviceId)
                .stream().map(cm -> cm.getColumnName())
                .toList();
        Map<String, String> requestParam = (Map<String, String>) messageBody.get("contentMap");

        try {
            String sql = sqlQueryBuilder.insert(new QueryBuilderParameter(tableName, columnNames, requestParam));
            sentMessageProcessStatus = SUCCESS;
            template.update(sql, Map.of());
        } catch (RuntimeException exception) {
            sentMessageProcessStatus = FAIL;
            log.error("메시지 변환 중 에러가 발생했습니다. 롤백합니다.", exception);
        } finally {
            messageQRepository.deleteById(payload.getPk());
            MessageQResult messageQResult = createSentMessageQResult(payload, sentMessageProcessStatus);
            messageQResultRepository.save(messageQResult);
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
}
