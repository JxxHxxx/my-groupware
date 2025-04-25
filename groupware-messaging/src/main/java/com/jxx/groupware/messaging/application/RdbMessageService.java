package com.jxx.groupware.messaging.application;

import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageColumnMapping;
import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageTableMapping;
import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.core.messaging.infra.MessageColumnMappingRepository;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.messaging.infra.MessageQResultRepository;
import com.jxx.groupware.core.messaging.infra.MessageTableMappingRepository;
import com.jxx.groupware.messaging.application.sql.builder.QueryBuilderParameter;
import com.jxx.groupware.messaging.application.sql.builder.SqlQueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

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

    @Override
    protected void processMessage(MessageQ payload) {
        MessageDestination messageDestination = payload.getMessageDestination();

        DataSource dataSource = destinationDataSourceMap.get(messageDestination.name());
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> messageBody = payload.getBody();
        String serviceId = String.valueOf(messageBody.get("SERVICEID"));

        MessageTableMapping tableMapping = tableMappingRepository.findByServiceId(serviceId).orElseThrow();
        String tableName = tableMapping.getTableName();

        List<String> columnNames = columnMappingRepository.findByServiceId(serviceId)
                .stream().map(MessageColumnMapping::getColumnName)
                .toList();

        Map<String, String> requestParam = (Map<String, String>) messageBody.get("contentMap");
        String sql = sqlQueryBuilder.insert(new QueryBuilderParameter(tableName, columnNames, requestParam));
        template.update(sql, Map.of()); // Map.of() 는 메서드를 호출하기 위해 넣는 껍데기 파라미터
    }
}
