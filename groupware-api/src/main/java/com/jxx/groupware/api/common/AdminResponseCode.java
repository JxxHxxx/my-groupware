package com.jxx.groupware.api.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminResponseCode {
    ADM_MSG_F_001("ADM:MSG:F:001", "admin-msg", "존재하지 않는 destinationId 입니다."),
    ADM_MSG_F_002("ADM:MSG:F:002", "admin-msg", "요청 받은 destinationId, tableName 으로 구성된 테이블 매핑 정보가 이미 존재합니다.");

    private final String code;
    private final String applicationDomain;
    private final String description;

}
