package com.jxx.groupware.api.messaging.dto.request;

import com.jxx.groupware.core.messaging.domain.destination.ConnectionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MessageQDestinationRequest {

    private final String destinationId;
    private final String destinationName;
    private final Map<String, Object> connectionInformation;
    private final ConnectionType connectionType;
}
