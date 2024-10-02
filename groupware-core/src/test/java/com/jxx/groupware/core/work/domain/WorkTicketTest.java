package com.jxx.groupware.core.work.domain;


import com.jxx.groupware.core.work.dto.TicketReceiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class WorkTicketTest {

    @DisplayName("isNotReceiverRequest 메서드는 " +
            "workDetail 엔티티의 receiverId, " +
            "workTicket 의 chargeCompanyId, chargeDepartmentId가 " +
            "파라미터인 TicketReceiver 의 receiverId, receiverCompanyId, receiverDepartmentId 와" +
            "일치하는지 검증한다.")
    @Test
    void is_not_receiver_request() {
        WorkTicket workTicket = WorkTicket.builder()
                .chargeDepartmentId("ORG0001")
                .chargeCompanyId("TEST")
                .build();

        WorkDetail workDetail = WorkDetail.builder()
                .receiverId("TEST00001")
                .build();

        workTicket.mappingWorkDetail(workDetail);
        // 잘못된 부서코드 입력 시
        assertThat(workTicket.isNotReceiverRequest(new TicketReceiver("TEST00001", "TEST", "ORG0002"))).isTrue();
        // 모든 정보가 잘못된 경우
        assertThat(workTicket.isNotReceiverRequest(new TicketReceiver("TEST00000", "TT", "ORG0002"))).isTrue();
        // 요청자의 요청 - 올바른 요청
        assertThat(workTicket.isNotReceiverRequest(new TicketReceiver("TEST00001", "TEST", "ORG0001"))).isFalse();
    }
}