package com.jxx.groupware.api.messaging.application;

import com.jxx.groupware.api.messaging.dto.request.DataSourceConnectionRequest;
import com.jxx.groupware.api.messaging.dto.request.MessageQDestinationRequest;
import com.jxx.groupware.api.messaging.dto.response.DataSourceConnectionResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageQDestinationResponse;
import com.jxx.groupware.core.common.pagination.PageService;
import com.jxx.groupware.core.messaging.domain.MessageClientException;
import com.jxx.groupware.core.messaging.domain.destination.ConnectionInformationValidator;
import com.jxx.groupware.core.messaging.domain.destination.ConnectionType;
import com.jxx.groupware.core.messaging.domain.destination.DefaultConnectionInformationValidator;
import com.jxx.groupware.core.messaging.domain.destination.MessageQDestination;
import com.jxx.groupware.core.messaging.domain.destination.dto.ConnectionInformationRequiredResponse;
import com.jxx.groupware.core.messaging.infra.MessageQDestinationRepository;
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
import java.util.List;
import java.util.Map;

import static com.jxx.groupware.core.messaging.domain.MessageResponseCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageDestinationService {

    private final MessageQDestinationRepository messageQDestinationRepository;

    @Transactional
    public MessageQDestinationResponse createDestination(MessageQDestinationRequest request) {
        final ConnectionType connectionType = ConnectionType.valueOf(request.getConnectionType());
        final Map<String, Object> connectionInformation = request.getConnectionInformation();

        // 필수 값 존재 검증
        ConnectionInformationValidator validator = new DefaultConnectionInformationValidator();
        ConnectionInformationRequiredResponse requiredResponse = validator.required(connectionType, connectionInformation);

        if (!requiredResponse.meet()) {
            log.error("connectionType:{} must have {} keys", connectionType, requiredResponse.notMatchedKeys());
            throw new MessageClientException(MSQF001.getCode(), MSQF001.getDescription());
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
                new DataSourceConnectionResponse(isActive,"연결 실패하였습니다. 서비스 상태 및 연결 정보를 확인해주세요." , responseTime);
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
}
