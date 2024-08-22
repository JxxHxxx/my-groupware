package com.jxx.groupware.api.work.application;

/**
  workTicket(작업 요청 티켓) 을 접수하려고 할 때
 발생하는 다른 도메인의 이벤트를 처리하기 위해 필요한 데이터를 가지고 있는 record 클래스

 여기서 말하는 다른 도메인이란 쉽게 jxx/groupware/api/work 디렉터리 내에 있지 않는
 클래스를 의존하는 경우를 말한다. 단 api/common 하위에 존재하는 클래스는 공통 클래스이기에 예외이다.
 1.

 **/
public record WorkTicketReceiveEvent(
        String receiverId,
        String receiverCompanyId,
        String receiverDepartmentId,
        String chargeCompanyId,
        String chargeDepartmentId
) {
}
