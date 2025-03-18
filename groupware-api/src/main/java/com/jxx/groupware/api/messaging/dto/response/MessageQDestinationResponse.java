package com.jxx.groupware.api.messaging.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MessageQDestinationResponse {
    private final String connectionType;
    private final Map<String, Object> connectionInformation;

    private final String destinationId;
    private final String destinationName;
    private final boolean used;
    private final LocalDateTime offDateTime;
    private final LocalDateTime createDateTime;

}
