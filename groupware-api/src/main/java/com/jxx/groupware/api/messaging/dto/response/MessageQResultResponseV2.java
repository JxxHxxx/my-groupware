package com.jxx.groupware.api.messaging.dto.response;

import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageQResultResponseV2 {
    private Long pk;
    private Long originalMessagePk;
    private MessageDestination messageDestination;
    private Map<String, Object> body;
    private MessageProcessStatus messageProcessStatus;
    private LocalDateTime eventTime;
    private LocalDateTime processStartTime;
    private LocalDateTime processEndTime;
    private MessageProcessType messageProcessType;
}
