package com.jxx.groupware.api.messaging.dto.request;

import com.jxx.groupware.core.message.domain.MessageDestination;
import com.jxx.groupware.core.message.domain.MessageProcessStatus;
import lombok.Getter;

@Getter
public class MessageQResultSearchCondition {
    private final String startDate;
    private final String endDate;
    private final MessageDestination messageDestination;
    private MessageProcessStatus messageProcessStatus;

    public MessageQResultSearchCondition(String startDate, String endDate, MessageDestination messageDestination) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.messageDestination = messageDestination;
    }

    public void setMessageProcessStatus(MessageProcessStatus messageProcessStatus) {
        this.messageProcessStatus = messageProcessStatus;
    }
}
