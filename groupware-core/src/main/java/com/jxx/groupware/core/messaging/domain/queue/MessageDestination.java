package com.jxx.groupware.core.messaging.domain.queue;

import lombok.Getter;

@Getter
public enum MessageDestination {
    CONFIRM("결재 서버"), GW_NOTIFICATION_DB("GW 알림 DB");

    private final String description;

    MessageDestination(String description) {
        this.description = description;
    }
}
