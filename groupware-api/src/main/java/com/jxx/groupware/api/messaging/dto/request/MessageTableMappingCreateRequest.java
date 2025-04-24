package com.jxx.groupware.api.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageTableMappingCreateRequest {

    @NotBlank(message = "serviceId는 공백일 수 없습니다")
    private final String serviceId;
    @NotBlank(message = "tableName은 공백일 수 없습니다")
    private final String tableName;
}
