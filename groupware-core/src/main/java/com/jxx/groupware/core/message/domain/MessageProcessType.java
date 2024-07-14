package com.jxx.groupware.core.message.domain;

public enum MessageProcessType {

    INSERT("DB_INSERT"),
    UPDATE("DB_UPDATE"),
    DELETE_SOFT("DB_DELETE_SOFT"),
    DELETE_HARD("DB_DELETE_HARD"),
    API("WEB_API");

    private final String description;

    MessageProcessType(String description) {
        this.description = description;
    }
}
