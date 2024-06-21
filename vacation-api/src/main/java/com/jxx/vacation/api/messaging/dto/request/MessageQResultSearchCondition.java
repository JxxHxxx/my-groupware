package com.jxx.vacation.api.messaging.dto.request;

import com.jxx.vacation.core.message.domain.MessageDestination;
import com.jxx.vacation.core.message.domain.MessageProcessStatus;
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
