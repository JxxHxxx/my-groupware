package com.jxx.groupware.api.work.application;

/**
 * <pre>작업티켓 생성 타 도메인 이벤트 처리 클래스</pre>
 * <pre>1.사용자 실재/활성화 여부 검증 (미구현)</pre>
 * <pre>2. 부서 실재/활성화 여부 검증</pre>
 **/
public record WorkTicketCreateEvent(
        String chargeCompanyId,
        String chargeDepartmentId
) {
}
