package com.jxx.groupware.api.messaging.application;

import com.jxx.groupware.api.messaging.dto.request.MessageColumnMappingCreateRequest;
import com.jxx.groupware.api.messaging.dto.request.DataSourceConnectionRequest;
import com.jxx.groupware.api.messaging.dto.request.MessageQDestinationRequest;
import com.jxx.groupware.api.messaging.dto.request.MessageTableMappingCreateRequest;
import com.jxx.groupware.api.messaging.dto.response.DataSourceConnectionResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageColumnMappingResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageQDestinationResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageTableMappingResponse;
import com.jxx.groupware.core.common.pagination.PageService;
import com.jxx.groupware.core.messaging.domain.destination.ConnectionInformationValidator;
import com.jxx.groupware.core.messaging.domain.destination.ConnectionType;
import com.jxx.groupware.core.messaging.domain.destination.DefaultConnectionInformationValidator;
import com.jxx.groupware.core.messaging.domain.destination.MessageQDestination;
import com.jxx.groupware.core.messaging.domain.destination.dto.ConnectionInformationRequiredResponse;
import com.jxx.groupware.core.messaging.domain.destination.rdb.DmlType;
import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageColumnMapping;
import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageTableMapping;
import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessType;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.core.messaging.infra.MessageColumnMappingRepository;
import com.jxx.groupware.core.messaging.infra.MessageQDestinationRepository;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.messaging.infra.MessageTableMappingRepository;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.jxx.groupware.api.common.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageDestinationService {

    private final MessageQDestinationRepository messageQDestinationRepository;
    private final MessageTableMappingRepository tableMappingRepository;
    private final MessageColumnMappingRepository columnMappingRepository;

    @Transactional
    public MessageQDestinationResponse createDestination(MessageQDestinationRequest request) {
        final ConnectionType connectionType = ConnectionType.valueOf(request.getConnectionType());
        final Map<String, Object> connectionInformation = request.getConnectionInformation();

        // 필수 값 존재 검증
        ConnectionInformationValidator validator = new DefaultConnectionInformationValidator();
        ConnectionInformationRequiredResponse requiredResponse = validator.required(connectionType, connectionInformation);

        if (!requiredResponse.meet()) {
            log.error("connectionType:{} must have {} keys", connectionType, requiredResponse.notMatchedKeys());
            throw new MessageAdminException(ADM_MSG_F_007);
        }

        MessageQDestination messageQDestination = MessageQDestination.builder()
                .destinationId(request.getDestinationId())
                .destinationName(request.getDestinationName())
                .connectionInformation(connectionInformation)
                .connectionType(connectionType)
                .createDateTime(LocalDateTime.now())
                .used(true)
                .offDateTime(null)
                .build();

        MessageQDestination savedMessageQDestination = messageQDestinationRepository.save(messageQDestination);

        return new MessageQDestinationResponse(
                savedMessageQDestination.getConnectionType().name(),
                savedMessageQDestination.getConnectionInformation(),
                savedMessageQDestination.getDestinationId(),
                savedMessageQDestination.getDestinationName(),
                savedMessageQDestination.getUsed(),
                savedMessageQDestination.getOffDateTime(),
                savedMessageQDestination.getCreateDateTime());
    }

    public DataSourceConnectionResponse isConnectionActivation(DataSourceConnectionRequest request) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(request.getDriverClassName());
        dataSource.setJdbcUrl(request.getJdbcUrl());
        dataSource.setUsername(request.getUserName());
        dataSource.setPassword(request.getPassword());

        // DB 연결 여부
        boolean isActive;
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT 1");

            isActive = rs.next();
            connection.close();
        } catch (SQLException e) {
            log.error("connection fail", e);
            isActive = false;
        } finally {
            dataSource.close();
        }
        LocalDateTime responseTime = LocalDateTime.now();
        return isActive ? new DataSourceConnectionResponse(isActive, "정상 연결 확인되었습니다", responseTime) :
                new DataSourceConnectionResponse(isActive, "연결 실패하였습니다. 서비스 상태 및 연결 정보를 확인해주세요.", responseTime);
    }

    public void selectByDestinationId() {

    }

    public void changeDestinationUsed(String destinationId) {
    }

    public PageImpl<MessageQDestinationResponse> search(int page, int size) {
        List<MessageQDestinationResponse> responses = messageQDestinationRepository.findAll().stream()
                .map(mqd -> new MessageQDestinationResponse(
                        mqd.getConnectionType().name(),
                        mqd.getConnectionInformation(),
                        mqd.getDestinationId(),
                        mqd.getDestinationName(),
                        mqd.getUsed(),
                        mqd.getOffDateTime(),
                        mqd.getCreateDateTime()))
                .toList();


        PageService pageService = new PageService(page, size);
        return pageService.convertToPage(responses);
    }

    public void deleteDestination() {

    }

    @Transactional
    public MessageTableMappingResponse createMessageTableMapping(String destinationId, MessageTableMappingCreateRequest request) {
        // 목적지 엔티티에 destinationId 가 존재하지 않을 경우 오류, 왜냐하면 TableMapping 과 목적지 엔티티를 이어주는 간접키가 destinationId 임
        messageQDestinationRepository.findByDestinationId(destinationId)
                .orElseThrow(() -> new MessageAdminException(ADM_MSG_F_001));
        final String serviceId = request.getServiceId();
        // 동일한 serviceId 레코드 있을 때 예외, 이 부분 처리하지 않아도 유니크 제약에 의해 오류 발생 500 에러를 400 에러로 변경하기 위함
        if (tableMappingRepository.findByServiceId(serviceId).isPresent()) {
            throw new MessageAdminException(ADM_MSG_F_002);
        }

        DmlType dmlType = DmlType.valueOf(request.getDmlType());
        MessageTableMapping tableMapping = MessageTableMapping.builder()
                .serviceId(serviceId)
                .destinationId(destinationId)
                .tableName(request.getTableName())
                .dmlType(dmlType)
                .createdTime(LocalDateTime.now())
                .lastModifiedTime(LocalDateTime.now())
                .used(true)
                .build();

        MessageTableMapping savedTableMapping = tableMappingRepository.save(tableMapping);
        return new MessageTableMappingResponse(
                savedTableMapping.getServiceId(),
                savedTableMapping.getDestinationId(),
                savedTableMapping.getTableName(),
                savedTableMapping.getDmlType().name(),
                savedTableMapping.isUsed(),
                savedTableMapping.getCreatedTime(),
                savedTableMapping.getLastModifiedTime());
    }

    @Transactional
    public MessageColumnMappingResponse createMessageColumnMapping(String destinationId, String serviceId,
                                                                   MessageColumnMappingCreateRequest request) {

        MessageTableMapping tableMapping = tableMappingRepository.findByServiceId(serviceId)
                .orElseThrow(() -> {
                    log.info(ADM_MSG_F_003.getErrorMessage());
                    return new MessageAdminException(ADM_MSG_F_003);
                });

        if (!Objects.equals(destinationId, tableMapping.getDestinationId())) {
            log.info(ADM_MSG_F_004.getErrorMessage());
            throw new MessageAdminException(ADM_MSG_F_004);
        }

        String columnName = request.getColumnName();

        if (columnMappingRepository.findByServiceIdAndColumnName(serviceId, columnName).isPresent()) {
            log.info(ADM_MSG_F_005.getErrorMessage());
            throw new MessageAdminException(ADM_MSG_F_005);
        }

        MessageColumnMapping columnMapping = MessageColumnMapping.builder()
                .messageTableMapping(tableMapping)
                .columnName(columnName)
                .columnType(request.getColumnType())
                .lastModifiedTime(LocalDateTime.now())
                .used(true)
                .build();

        MessageColumnMapping savedMessageColumnMapping = columnMappingRepository.save(columnMapping);

        return new MessageColumnMappingResponse(
                savedMessageColumnMapping.getMessageColumnMappingPk(),
                savedMessageColumnMapping.receiveDestinationId(),
                savedMessageColumnMapping.getColumnName(),
                savedMessageColumnMapping.getColumnType(),
                savedMessageColumnMapping.getLastModifiedTime(),
                savedMessageColumnMapping.isUsed());
    }

    private final MessageQRepository messageQRepository;

    @Transactional
    public void test() {
        Map<String, Object> body = new HashMap<>();
        body.put("SERVICEID", "CREATE_NOTIFICATION");
        Map<String, Object> values = new HashMap<>();
        values.put("MEMBER_ID", "jxxHxxx");
        values.put("CONTENT", "테스트");

        body.put("contentMap", values);

        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.GW_NOTIFICATION_DB)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .messageProcessType(MessageProcessType.RDB)
                .body(body)
                .build();

        messageQRepository.save(messageQ);
    }

    @Transactional
    public void testV2() {
        Map<String, Object> body = new HashMap<>();
        body.put("SERVICEID", "CREATE_NOTIFICATION");
        Map<String, Object> values = new HashMap<>();
        values.put("2", "jxxHxxx");
        values.put("4", "테스트");
        values.put("7", "1테스트");

        body.put("contentMap", values);

        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.GW_NOTIFICATION_DB)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .messageProcessType(MessageProcessType.RDB)
                .body(body)
                .build();

        messageQRepository.save(messageQ);
    }

    @Transactional
    public void update() {
        Map<String, Object> body = new HashMap<>();
        body.put("SERVICEID", "UPDATE_NOTIFICATION");
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("CONTENT", "내용을 변경한다!");

        body.put("contentMap", contentMap);

        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("MEMBER_ID", "U00003");

        body.put("whereMap", whereMap);

        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.GW_NOTIFICATION_DB)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .messageProcessType(MessageProcessType.RDB)
                .body(body)
                .build();

        messageQRepository.save(messageQ);
    }

    @Transactional
    public void updateV2() {
        Map<String, Object> body = new HashMap<>();
        body.put("SERVICEID", "UPDATE_NOTIFICATION");
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("CONTENT", "내용을 변경한다!");

        body.put("contentMap", contentMap);

        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("MEMBER_ID", "U00003");

        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.GW_NOTIFICATION_DB)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .messageProcessType(MessageProcessType.RDB)
                .body(body)
                .build();

        messageQRepository.save(messageQ);
    }
}
