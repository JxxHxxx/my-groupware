package com.jxx.groupware.api.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ADM_MSG_F_001("ADM:MSG:F:001", 400, "존재하지 않는 destinationId 입니다."),
    ADM_MSG_F_002("ADM:MSG:F:002", 400, "요청 받은 serviceId 로 구성된 테이블 매핑 정보가 이미 존재합니다."),
    COM_AUTH_F_001("COM:AUTH:F:001", 401, "인증되지 않은 클라이언트가 요청하였습니다"),
    COM_AUTH_F_002("COM:AUTH:F:002", 403, "요청에 대한 권한이 존재하지 않습니다");

    private final String errorCode;
    private final int statusCode;
    private final String errorMessage;

}
