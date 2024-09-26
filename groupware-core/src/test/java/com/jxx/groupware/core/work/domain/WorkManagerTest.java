package com.jxx.groupware.core.work.domain;


import com.jxx.groupware.core.work.domain.exception.WorkClientException;
import com.jxx.groupware.core.work.dto.TicketReceiver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

class WorkManagerTest {


    @DisplayName("작업 접수자의 작업 티켓 반려 성공 케이스" +
            "작업 티켓 접수자는 WorkTicket 의 WorkStatus 가 RECEIVE, ANALYZE_BEGIN, ANALYZE_COMPLETE 중 하나일 때만 " +
            "작업 티켓을 반려할 수 있다. 정상적으로 반려하게 되는 경우, " +
            "작업 티켓의 WorkStatus 는 REJECT_FROM_CHARGE 으로 변경된다.")
    @EnumSource(names = {"RECEIVE", "ANALYZE_BEGIN", "ANALYZE_COMPLETE"})
    @ParameterizedTest
    void reject_from_receiver_success_case(WorkStatus rejectFromReceiverPossibleWorkStatus) {
        //given
        // 티켓 접수자
        TicketReceiver ticketReceiver = new TicketReceiver("SPY00009", "SPY", "SPY00035");
        // 티켓 요청자
        WorkRequester workRequester = new WorkRequester("SPY", "SPY00001", "요청자1");
        WorkTicket workTicket = WorkTicket.builder()
                .workStatus(rejectFromReceiverPossibleWorkStatus)
                .chargeCompanyId(ticketReceiver.receiverCompanyId())
                .chargeDepartmentId(ticketReceiver.receiverDepartmentId())
                .workRequester(workRequester)
                .build();

        WorkDetail workDetail = WorkDetail.builder()
                .receiverId(ticketReceiver.receiverId())
                .build();
        workTicket.mappingWorkDetail(workDetail);
        WorkManager workManager = new WorkManager(workTicket);

        //when - then - 1
        assertThatCode(() -> workManager.rejectFromReceiver("반려 테스트", ticketReceiver))
                .doesNotThrowAnyException();

        //then - 2
        WorkStatus updatedWorkStatus = workTicket.getWorkStatus();
        assertThat(updatedWorkStatus).isEqualTo(WorkStatus.REJECT_FROM_CHARGE);
    }

    @DisplayName("작업 접수자의 작업 티켓 반려 실패 케이스 - 반려할 수 없는 WorkStatus 상태인 경우, WorkClientException 예외 발생")
    @EnumSource(names = {"RECEIVE", "ANALYZE_BEGIN", "ANALYZE_COMPLETE"}, mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void reject_from_receiver_fail_case_1(WorkStatus rejectFromReceiverPossibleWorkStatus) {
        //given
        // 티켓 접수자
        TicketReceiver ticketReceiver = new TicketReceiver("SPY00009", "SPY", "SPY00035");
        // 티켓 요청자
        WorkRequester workRequester = new WorkRequester("SPY", "SPY00001", "요청자1");
        WorkTicket workTicket = WorkTicket.builder()
                .workStatus(rejectFromReceiverPossibleWorkStatus)
                .chargeCompanyId(ticketReceiver.receiverCompanyId())
                .chargeDepartmentId(ticketReceiver.receiverDepartmentId())
                .workRequester(workRequester)
                .build();

        WorkDetail workDetail = WorkDetail.builder()
                .receiverId(ticketReceiver.receiverId())
                .build();
        workTicket.mappingWorkDetail(workDetail);
        WorkManager workManager = new WorkManager(workTicket);

        //when - then - 1
        assertThatThrownBy(() -> workManager.rejectFromReceiver("반려 테스트", ticketReceiver))
                .isInstanceOf(WorkClientException.class)
                .hasMessageContaining("반려할 수 없는 티켓입니다.");
        // then - 2 workStatus 상태는 기존대로 유지된다.
        assertThat(workTicket.getWorkStatus()).isEqualTo(rejectFromReceiverPossibleWorkStatus);
    }

    @DisplayName("작업 접수자의 작업 티켓 반려 실패 케이스 - 티켓 접수자가 아닌 경우 WorkClientException 예외 발생")
    @Test
    void reject_from_receiver_fail_case_2() {
        //given
        // 티켓 접수자
        TicketReceiver ticketReceiver = new TicketReceiver("SPY00009", "SPY", "SPY00035");
        // 티켓 요청자
        WorkRequester workRequester = new WorkRequester("SPY", "SPY00001", "요청자1");
        WorkTicket workTicket = WorkTicket.builder()
                .workStatus(WorkStatus.RECEIVE)
                .chargeCompanyId(ticketReceiver.receiverCompanyId())
                .chargeDepartmentId(ticketReceiver.receiverDepartmentId())
                .workRequester(workRequester)
                .build();

        String anotherReceiverId = "SPY00035"; // 접수자와 다른 ID , 이외에도 회사ID, 부서ID도 다를 경우 검증 실패
        WorkDetail workDetail = WorkDetail.builder()
                .receiverId(anotherReceiverId)
                .build();
        workTicket.mappingWorkDetail(workDetail);
        WorkManager workManager = new WorkManager(workTicket);

        assertThatThrownBy(() -> workManager.rejectFromReceiver("반려 테스트", ticketReceiver))
                .isInstanceOf(WorkClientException.class)
                .hasMessageContaining("접수자가 아닌 사용자가 반려하려고 합니다.");
    }
}