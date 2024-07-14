package com.jxx.groupware.core.common.history;

public enum TaskType {
    I("INSERT", "생성"),
    U("UPDATE", "수정"),
    D("DELETE", "삭제");

    private final String sqlType;
    private final String description;

    TaskType(String sqlType, String description) {
        this.sqlType = sqlType;
        this.description = description;
    }
}
