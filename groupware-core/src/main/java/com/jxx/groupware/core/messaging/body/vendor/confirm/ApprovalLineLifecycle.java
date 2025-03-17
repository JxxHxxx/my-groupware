package com.jxx.groupware.core.messaging.body.vendor.confirm;

// 결재 애플리케이션 코드
public enum ApprovalLineLifecycle {

    BEFORE_CREATE("결재선 생성 전"),
    CREATED("결재선 생성 완료 "),
    PROCESS_MODIFIABLE("결재 진행중:변경 가능"),
    PROCESS_UNMODIFIABLE("결재 진행중:변경 불가능");

    private final String description;

    ApprovalLineLifecycle(String description) {
        this.description = description;
    }
}
