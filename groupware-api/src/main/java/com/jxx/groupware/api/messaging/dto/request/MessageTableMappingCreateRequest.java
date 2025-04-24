package com.jxx.groupware.api.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageTableMappingCreateRequest {

    @NotBlank
    private final String serviceId;
    @NotBlank
    private final String tableName;
}
