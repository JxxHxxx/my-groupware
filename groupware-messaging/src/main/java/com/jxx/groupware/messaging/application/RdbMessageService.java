package com.jxx.groupware.messaging.application;

import com.jxx.groupware.core.messaging.domain.NonUniqueWriteException;
import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageColumnMapping;
import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageTableMapping;
import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.core.messaging.infra.MessageColumnMappingRepository;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.messaging.infra.MessageQResultRepository;
import com.jxx.groupware.core.messaging.infra.MessageTableMappingRepository;
import com.jxx.groupware.messaging.application.sql.builder.InsertBuilderParameter;
import com.jxx.groupware.messaging.application.sql.builder.RdbMessagePolicyException;
import com.jxx.groupware.messaging.application.sql.builder.SqlQueryBuilder;
import com.jxx.groupware.messaging.application.sql.builder.UpdateBuilderParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


@Slf4j
@Service(value = "rdbMessageService")
public class RdbMessageService extends AbstractMessageService {

    private final Map<String, DataSource> destinationDataSourceMap;
    private final MessageTableMappingRepository tableMappingRepository;
    private final MessageColumnMappingRepository columnMappingRepository;
    private final SqlQueryBuilder sqlQueryBuilder;

    public RdbMessageService(MessageQRepository messageQRepository, MessageQResultRepository messageQResultRepository,
                             @Qualifier("destinationDataSourceMap") Map<String, DataSource> destinationDataSourceMap,
                             MessageTableMappingRepository tableMappingRepository,
                             MessageColumnMappingRepository columnMappingRepository,
                             SqlQueryBuilder sqlQueryBuilder) {
        super(messageQRepository, messageQResultRepository);
        this.destinationDataSourceMap = destinationDataSourceMap;
        this.tableMappingRepository = tableMappingRepository;
        this.columnMappingRepository = columnMappingRepository;
        this.sqlQueryBuilder = sqlQueryBuilder;
    }

    // 연동된 DB에 쓰기 작업을 하므로 트랜잭션을 별도로 관리합니다.
    // 예를 들어 consumer 가 소비를 실패하는 경우, 연동된 DB에는 쓰기작업이 커밋되면 안됩니다.
    // 하지만 메시징 시스템과 연동된 DB에는 consumer 가 소비 실패로 인해 MessageQ, MessageQHistory 엔티티의 변화가 일어나고 이에 대한 커밋이 DB에 이뤄져야 합니다.
    @Override
    protected void processMessage(MessageQ payload) {
        MessageDestination messageDestination = payload.getMessageDestination();
        // DataSource 선택 및 트랜잭션 생성
        DataSource dataSource = destinationDataSourceMap.get(messageDestination.name());
        JdbcTransactionManager txManager = new JdbcTransactionManager(dataSource);
        TransactionStatus txStatus = txManager.getTransaction(TransactionDefinition.withDefaults());

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> messageBody = payload.getBody();
        String serviceId = String.valueOf(messageBody.get("SERVICEID"));

        MessageTableMapping tableMapping = tableMappingRepository.findByServiceId(serviceId).orElseThrow();
        String tableName = tableMapping.getTableName();
        List<String> columnNames = columnMappingRepository.findByServiceId(serviceId)
                .stream().map(MessageColumnMapping::getColumnName)
                .toList();

        Map<String, String> requestParam = (Map<String, String>) messageBody.get("contentMap");
        switch (tableMapping.getDmlType()) {
            case INSERT -> {
                String sql = sqlQueryBuilder.insert(new InsertBuilderParameter(tableName, columnNames, requestParam));
                template.update(sql, Map.of()); // Map.of() 는 메서드를 호출하기 위해 넣는 껍데기 파라미터
            }
            case UPDATE -> {
                Map<String, String> whereParam = (Map<String, String>) messageBody.get("whereMap");
                String sql = null;
                try {
                    sql = sqlQueryBuilder.update(new UpdateBuilderParameter(tableName, columnNames, requestParam, whereParam));
                    int update = template.update(sql, Map.of());

                    if (update > 1) {
                        txStatus.setRollbackOnly();
                        throw new NonUniqueWriteException(update + "개의 레코드에 변경이 일어나게 됩니다. RDB 정책에 위배되어 롤백합니다.");
                    }
                } catch (RdbMessagePolicyException exception) {
                    log.error("{}", exception.getMessage(), exception);
                    txStatus.setRollbackOnly();
                } catch (Exception e) {
                    log.error("예외 발생 롤백합니다.", e);
                    txStatus.setRollbackOnly();
                }
            }
            case DELETE -> {
            }
            default -> {
            }
        }

        txManager.commit(txStatus);
    }
}
