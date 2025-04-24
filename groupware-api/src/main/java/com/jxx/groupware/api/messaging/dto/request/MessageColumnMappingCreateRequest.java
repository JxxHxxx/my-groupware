package com.jxx.groupware.api.messaging.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageColumnMappingCreateRequest {

    @NotBlank(message = "columnName은 공백일 수 없습니다")
    private final String columnName;
    @NotBlank(message = "columnType은 공백일 수 없습니다")
    private final String columnType;
    @NotBlank(message = "messageProcessType은 공백일 수 없습니다")
    private final String messageProcessType;

}
