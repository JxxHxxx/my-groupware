package com.jxx.vacation.core.message.payload.approval;

import lombok.Getter;

@Getter
public enum DocumentType {
    VAC("휴가 요청"),
    DCR("데이터 변경 요청"),
    COST("견적서");

    private final String description;

    DocumentType(String description) {
        this.description = description;
    }
}
