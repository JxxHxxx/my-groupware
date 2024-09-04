package com.jxx.groupware.core.message.domain;

import java.util.List;

public enum MessageProcessType {

    INSERT("DB_INSERT"),
    UPDATE("DB_UPDATE"),
    DELETE_SOFT("DB_DELETE_SOFT"),
    DELETE_HARD("DB_DELETE_HARD"),
    REST("REST_API");

    private final String description;

    MessageProcessType(String description) {
        this.description = description;
    }

    private static final List<MessageProcessType> DB_PROCESS_TYPES = List.of(INSERT, UPDATE, DELETE_HARD, DELETE_SOFT);
    public boolean isDBProcessType() {
        return DB_PROCESS_TYPES.contains(this);
    }

}
