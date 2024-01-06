package com.jxx.vacation.core.message.payload.approval;

import lombok.Getter;

@Getter
public enum ApprovalStatus {

    CREATE("결재 생성"),
    UPDATE("결재 수정"),
    RAISE("결재 상신"),
    CANCEL("결재 취소");

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }
}
