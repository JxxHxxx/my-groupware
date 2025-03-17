package com.jxx.groupware.core.messaging.domain.destination.dto;

import com.jxx.groupware.core.messaging.domain.destination.ConnectionType;

import java.util.List;

public record ConnectionInformationRequiredResponse(
        boolean meet,
        ConnectionType connectionType,
        List<String> notMatchedKeys
) {
}
