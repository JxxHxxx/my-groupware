package com.jxx.groupware.api.messaging.dto.response;

import java.time.LocalDateTime;

public record MessageColumnMappingResponse(
    Long messageColumnMappingPk,
    String destinationId,
    String columnName,
    String columnType,
    LocalDateTime lastModifiedTime,
    boolean used

) {
}
