package com.jxx.groupware.core.messaging.domain.destination;

import com.jxx.groupware.core.messaging.domain.destination.dto.ConnectionInformationRequiredResponse;

import java.util.Map;

public interface ConnectionInformationValidator {
    ConnectionInformationRequiredResponse required(ConnectionType connectionType, Map<String, Object> connectionInformation);
}

