package com.jxx.groupware.api.messaging.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class MessageTableMappingResponse {
    private final String destinationId;
    private final String tableName;
    private final boolean used;
    private final LocalDateTime createdTime;
    private final LocalDateTime lastModifiedTime;
}
