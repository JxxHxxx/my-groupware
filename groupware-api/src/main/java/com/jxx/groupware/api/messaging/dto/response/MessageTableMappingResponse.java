package com.jxx.groupware.api.messaging.dto.response;

import java.time.LocalDateTime;

public record MessageTableMappingResponse(
        String serviceId,
        String destinationId,
        String tableName,
        String dmlType,
        boolean used,
        LocalDateTime createdTime,
        LocalDateTime lastModifiedTime
) {
}
