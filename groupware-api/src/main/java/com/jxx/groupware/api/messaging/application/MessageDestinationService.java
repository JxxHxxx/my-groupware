package com.jxx.groupware.api.messaging.application;

import com.jxx.groupware.api.messaging.dto.request.MessageQDestinationRequest;
import com.jxx.groupware.core.messaging.domain.destination.ConnectionInformationValidator;
import com.jxx.groupware.core.messaging.domain.destination.ConnectionType;
import com.jxx.groupware.core.messaging.domain.destination.DefaultConnectionInformationValidator;
import com.jxx.groupware.core.messaging.domain.destination.MessageQDestination;
import com.jxx.groupware.core.messaging.domain.destination.dto.ConnectionInformationRequiredResponse;
import com.jxx.groupware.core.messaging.infra.MessageQDestinationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageDestinationService {

    private final MessageQDestinationRepository messageQDestinationRepository;

    @Transactional
    public void createDestination(MessageQDestinationRequest request) {
        final ConnectionType connectionType = request.getConnectionType();
        final Map<String, Object> connectionInformation = request.getConnectionInformation();

        // 검증
        ConnectionInformationValidator validator = new DefaultConnectionInformationValidator();
        ConnectionInformationRequiredResponse requiredResponse = validator.required(connectionType, connectionInformation);

        if (!requiredResponse.meet()) {
            log.error("connectionType:{} must have {} keys", connectionType, requiredResponse.notMatchedKeys());
            throw new MessageClientException();
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

        messageQDestinationRepository.save(messageQDestination);
    }

    public void selectByDestinationId() {

    }

    public void changeDestinationUsed() {

    }

    public void search() {

    }

    public void deleteDestination() {

    }
}
