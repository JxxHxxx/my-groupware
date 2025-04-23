package com.jxx.groupware.api.messaging.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageTableMappingCreateRequest {
    private String tableName;
}
