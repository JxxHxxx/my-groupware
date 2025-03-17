package com.jxx.groupware.core.messaging.domain;

import lombok.Getter;

@Getter
public enum MessageDestination {
    CONFIRM("결재 서버");

    private final String description;

    MessageDestination(String description) {
        this.description = description;
    }
}
