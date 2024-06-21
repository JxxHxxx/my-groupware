package com.jxx.vacation.api.messaging.dto.response;

import com.jxx.vacation.core.message.domain.MessageDestination;
import com.jxx.vacation.core.message.domain.MessageProcessStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
public class MessageQResultResponseV2 {
    private Long pk;
    private Long originalMessagePk;
    private MessageDestination messageDestination;
    private Map<String, Object> body;
    private MessageProcessStatus messageProcessStatus;
    private LocalDateTime eventTime;
    private LocalDateTime processStartTime;
    private LocalDateTime processEndTime;
}
