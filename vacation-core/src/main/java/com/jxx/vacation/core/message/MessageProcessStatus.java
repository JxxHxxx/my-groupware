package com.jxx.vacation.core.message;

public enum MessageProcessStatus {

    SENT("전송 완료"), // JXX_MESSAGE_Q 에 쌓이는 최초 상태
    PROCESS("처리 중"), //
    SUCCESS("처리 완료"),
    FAIL("처리 실패");

    private final String description;

    MessageProcessStatus(String description) {
        this.description = description;
    }
}
