package com.jxx.groupware.core.messaging.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageResponseCode {

    MSQS001("MSQ:S:001", "messaging", "요청을 정상적으로 처리했습니다"),
    MSQF001("MSQ:F:001", "messaging", "메시지Q 목적지 타입의 필수 파라미터가 누락되었습니다");
    private final String code;
    private final String applicationDomain;
    private final String description;

}
