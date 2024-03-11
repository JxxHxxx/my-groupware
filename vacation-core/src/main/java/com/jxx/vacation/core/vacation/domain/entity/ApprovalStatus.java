package com.jxx.vacation.core.vacation.domain.entity;

public enum ApprovalStatus {
    PEDNING("대기 중"),
    REJECT("결재 반려"), // 걸재 서버에서 결정
    APPROVED("결재 승인"); // 결재 서버에서 결정

    private final String description;

    ApprovalStatus(String description) {
        this.description = description;
    }
}
