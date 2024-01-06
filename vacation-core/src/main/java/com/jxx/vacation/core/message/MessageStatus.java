package com.jxx.vacation.core.message;

public enum MessageStatus {

    SENT("전송 완료"),
    PROCESS("처리 중"),
    SUCCESS("처리 완료"),
    FAIL("처리 실패");

    private final String description;

    MessageStatus(String description) {
        this.description = description;
    }
}
