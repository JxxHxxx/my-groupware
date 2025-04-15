package com.jxx.groupware.core.work;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkResponseCode {
    WORK_S_001("WORK:S:001", "api-work", "요청을 정상적으로 처리했습니다"),
    WORK_F_001("WORK:F:001", "api-work","잘못된 접근입니다. 권한이 존재하지 않습니다"),
    WORK_F_002("WORK:F:002","api-work", "중복 요청입니다. 요청 Token(클라이언트 측의 requestUUID 등을 확인하세요)"),
    WORK_F_003("WORK:F:003","api-work", "올바르지 않은 요청 Token 형식입니다");

    private final String code;
    private final String applicationDomain;
    private final String description;
}
