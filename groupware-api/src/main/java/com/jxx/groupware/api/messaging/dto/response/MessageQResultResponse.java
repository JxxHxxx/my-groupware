package com.jxx.groupware.api.messaging.dto.response;

import com.jxx.groupware.core.message.domain.MessageDestination;
import com.jxx.groupware.core.message.domain.MessageProcessStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record MessageQResultResponse(
        Long pk,
        Long originalMessagePk,
        MessageDestination messageDestination,
        Map<String, Object> body,
        MessageProcessStatus messageProcessStatus,
        LocalDateTime eventTime,
        LocalDateTime processStartTime,
        LocalDateTime processEndTime
) {
}
