package com.jxx.vacation.api.messaging.dto.response;

import com.jxx.vacation.core.message.MessageDestination;
import com.jxx.vacation.core.message.MessageProcessStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

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
