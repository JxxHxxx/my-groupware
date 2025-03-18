package com.jxx.groupware.api.messaging.dto.request;

import com.jxx.groupware.core.messaging.domain.destination.ConnectionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MessageQDestinationRequest {

    @NotBlank
    private final String destinationId;
    @NotBlank
    private final String destinationName;
    @NotEmpty
    private final Map<String, Object> connectionInformation;
    @NotBlank
    private final String connectionType;
}
