package com.jxx.groupware.api.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ADM_MSG_F_001("ADM:MSG:F:001", 400, "존재하지 않은 destinationId 입니다."),
    ADM_MSG_F_002("ADM:MSG:F:002", 422, "요청 받은 serviceId 로 구성된 테이블 매핑 정보가 이미 존재합니다."),
    ADM_MSG_F_003("ADM:MSG:F:003", 400, "존재하지 않는 serviceId 입니다."),
    ADM_MSG_F_004("ADM:MSG:F:001", 400, "요청 받은 destinationId 과 실제 destinationId 가 일치하지 않습니다"),
    ADM_MSG_F_005("ADM:MSG:F:005", 422, "요청 받은 serviceId, columnName, messageProcessType 으로 구성된 컬럼 매핑 정보가 이미 존재합니다"),
    ADM_MSG_F_006("ADM:MSG:F:006", 422, "재처리 할 수 없는 메시지입니다. 메시지 상태가 UNPROCESSABLE"),
    ADM_MSG_F_007("ADM:MSG:F:007", 400, "메시지Q 목적지 타입의 필수 파라미터가 누락되었습니다"),

    COM_AUTH_F_001("COM:AUTH:F:001", 401, "인증되지 않은 클라이언트가 요청하였습니다"),
    COM_AUTH_F_002("COM:AUTH:F:002", 403, "요청에 대한 권한이 존재하지 않습니다"),
    COM_API_F_001("COM:API:F:001", 400, "공백 혹은 NULL 값을 받을 수 없는 파라미터입니다"); // 수동 사용보다는 에러메시지를 받는게 나아보임

    private final String errorCode;
    private final int statusCode;
    private final String errorMessage;

}
