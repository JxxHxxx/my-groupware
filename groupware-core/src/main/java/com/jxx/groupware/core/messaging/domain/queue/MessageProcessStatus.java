package com.jxx.groupware.core.messaging.domain.queue;

import java.util.Objects;

public enum MessageProcessStatus {

    SENT("전송 완료", 1000), // JXX_MESSAGE_Q 에 쌓이는 최초 상태
    RETRY("재시도", 2000), // retry channel 에서 폴링
    PROCESS("처리 중", 3001), // producer 를 지났을 때 상태

    SUCCESS("처리 완료", 3002), // consumer 가 정상적으로 메시지를 처리한 경우
    // 예를 들어 RestApi 타입 메시지에서 api url 이 잘못된 경우
    // 다른 예로는 RDB 타입 메시지에서 INSERT INTO {TABLE_NAME} ('COL1' , 'COL2') VALUES (1, 2, 3, 4) 처럼 SQL 문법 상 오류가 있을 경우
    UNPROCESSABLE("처리할 수 없음", 3009), // 메시징 시스템 구조/비즈니스 로직 상 처리할 수 없는 문제
    ALREADY("이미 처리된 요청", 4000), // 재시도 API 호출 시, 이미 처리 완료된 메시지를 처리하려고 할 때
    FAIL("처리 실패", 5000); // consumer 에서 정상적으로 처리하지 못한 경우 : 정의 상 - 재시도가 가능하며 외부 환경에 의해 실패되었을 때 FAIL 을 사용해야 한다.



    private final String description;
    private final Integer statusCode;

    MessageProcessStatus(String description, Integer statusCode) {
        this.description = description;
        this.statusCode = statusCode;
    }

    public boolean isUnProcessable() {
        return Objects.equals(this, UNPROCESSABLE);
    }
}
