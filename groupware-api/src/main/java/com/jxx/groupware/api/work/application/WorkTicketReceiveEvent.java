package com.jxx.groupware.api.work.application;

/**
 * <pre>작업티켓 접수 타 도메인 이벤트 처리 클래스</pre>
 * <pre>1. 사용자 존재/활성화 여부 검증</pre>
 * <pre>2. 접수자의 실제 부서와 클라이언트에서 보내는 부서 정보가 일치하는지 여부 검증</pre>
 * <pre>3. 접수자가 해당 티켓을 접수할 수 있는 부서에 소속되어 있는지 검증</pre>
 **/
public record WorkTicketReceiveEvent(
        String receiverId,
        String receiverCompanyId,
        String receiverDepartmentId,
        String chargeCompanyId,
        String chargeDepartmentId
) {
}
