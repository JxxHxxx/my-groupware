package com.jxx.groupware.core.messaging.domain;

public enum MessageProcessStatus {

    SENT("전송 완료", 1000), // JXX_MESSAGE_Q 에 쌓이는 최초 상태
    RETRY("재시도", 2000),
    PROCESS("처리 중", 3001), //
    SUCCESS("처리 완료", 3002),
    ALREADY("이미 처리된 요청", 4000),
    FAIL("처리 실패", 5000);

    private final String description;
    private final Integer statusCode;

    MessageProcessStatus(String description, Integer statusCode) {
        this.description = description;
        this.statusCode = statusCode;
    }
}
