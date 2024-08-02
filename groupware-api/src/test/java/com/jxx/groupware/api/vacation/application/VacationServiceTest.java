package com.jxx.groupware.api.vacation.application;


import com.jxx.groupware.api.common.web.ServerCommunicationException;
import com.jxx.groupware.api.vacation.application.function.ConfirmRaiseApiAdapter;
import com.jxx.groupware.api.vacation.dto.request.RequestVacationForm;
import com.jxx.groupware.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.groupware.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.groupware.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.groupware.core.message.body.vendor.confirm.ConfirmStatus;
import com.jxx.groupware.core.message.domain.MessageQ;
import com.jxx.groupware.core.message.infra.MessageQRepository;
import com.jxx.groupware.core.vacation.domain.dto.RequestVacationDuration;
import com.jxx.groupware.core.vacation.domain.dto.UpdateVacationForm;
import com.jxx.groupware.core.vacation.domain.dto.VacationDurationDto;
import com.jxx.groupware.core.vacation.domain.entity.*;
import com.jxx.groupware.core.vacation.domain.exeception.VacationClientException;
import com.jxx.groupware.core.vacation.infra.MemberLeaveHistRepository;
import com.jxx.groupware.core.vacation.infra.MemberLeaveRepository;
import com.jxx.groupware.core.vacation.infra.OrganizationRepository;
import com.jxx.groupware.core.vacation.infra.VacationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import static com.jxx.groupware.core.common.generator.ConfirmDocumentIdGenerator.*;
import static com.jxx.groupware.core.message.body.vendor.confirm.ApprovalLineLifecycle.BEFORE_CREATE;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

@Slf4j
@SpringBootTest
@Transactional
class VacationServiceTest {

    @Autowired
    VacationService vacationService;
    @Autowired
    VacationRepository vacationRepository;
    @Autowired
    MemberLeaveRepository memberLeaveRepository;
    @Autowired
    MemberLeaveHistRepository memberLeaveHistRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    MessageQRepository messageQRepository;

    @BeforeEach
    void beforeEach() {
        memberLeaveHistRepository.deleteAll();
        memberLeaveRepository.deleteAll();
        organizationRepository.deleteAll();
        vacationRepository.deleteAll();
        messageQRepository.deleteAll();

        Organization organization = new Organization("O0001", "TJX", "TJ0001", "테스트부서", "TOP", "최상위부서");
        MemberLeave memberLeave = MemberLeave.builder()
                .memberId("T0001")
                .name("나재헌")
                .experienceYears(1)
                .isActive(true)
                .enteredDate(LocalDate.of(2023, 8, 16))
                .leave(new Leave(15F, 15F))
                .organization(organization)
                .build();

        organizationRepository.save(organization);
        memberLeaveRepository.save(memberLeave);
    }

    @AfterEach
    void afterEach() {
        vacationRepository.deleteAll();
        memberLeaveRepository.deleteAll();
        organizationRepository.deleteAll();
        messageQRepository.deleteAll();
    }

    @DisplayName("휴가 생성 통합 테스트" +
            "1. 휴가 신청을 정상적으로 마칠 경우 응답과 함께 비동기로 결재 서버에 결재 문서 초안을 위해 MessageQ 를 전달한다." +
            "2. 이에 따라 3가지를 검증한다. (1) 휴가 신청 응답 (2) 엔티티 저장 여부 (3) 비동기 메시지 검증" +
            "3. (1) 휴가 신청 응답 구조는 VacationServiceResponse 참고 " +
            "   (2) 저장된 휴가 엔티티의 차감 여부를 나타내는 LeaveDeduct 필드는 DEDUCT" +
            "   (3) 메시지 결재 문서 상태를 의미하는 ConfirmStatus = CREATE, " +
            " 결재 라인 생성 주기를 의미하는 APPROVAL_LIFE_CYCLE = BEFORE_CREATE " +
            " 결재 문서 ID 값은 'VAC' + companyId + vacationId 를 따른다.")
    @Test
    void create_vacation_success_case() {
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        RequestVacationForm vacationForm = new RequestVacationForm(
                memberLeave.getMemberId(), VacationType.MORE_DAY, LeaveDeduct.DEDUCT,
                List.of(new RequestVacationDuration(
                        LocalDateTime.of(2024, 3, 1, 0, 0),
                        LocalDateTime.of(2024, 3, 5, 0, 0))
                ), "휴가신청서",
                "개인사정",
                "이재헌",
                "위임자ID",
                "위임자명",
                "테스트 부서 ID",
                "테스트 부서 명"
        );

        // WHEN
        vacationService.createVacation(vacationForm);

        // 비동기 task 호출 확인
        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            List<MessageQ> messages = messageQRepository.findWithLimit(1);
            return !messages.isEmpty(); // 비어있지 않으면 호출되었단느 의미임
        });
        // THEN
        List<VacationServiceResponse> responses = vacationService.readByRequesterId(memberId);

        // 응답 검증

        assertThat(responses).extracting(response -> response.vacationDuration().stream().map(dto -> dto.startDateTime()).toList())
                .contains(List.of(LocalDateTime.of(2024, 3, 1, 0, 0)));

        assertThat(responses).extracting(response -> response.vacationDuration().stream().map(dto -> dto.endDateTime()).toList())
                .contains(List.of(LocalDateTime.of(2024, 3, 5, 0, 0)));

        assertThat(responses).extracting("vacationStatus")
                .containsExactly(VacationStatus.CREATE);
        assertThat(responses).extracting("requesterId")
                .containsExactly(memberId);

        // 저장된 엔티티 검증
        Long vacationId = responses.get(0).vacationId();
        Vacation savedVacation = vacationRepository.findById(vacationId).get();
        assertThat(savedVacation).isNotNull();
        assertThat(savedVacation.getLeaveDeduct()).isEqualTo(LeaveDeduct.DEDUCT);

        // messageQ 검증
        List<MessageQ> messages = messageQRepository.findWithLimit(1);
        String confirmDocumentId = ConfirmDocumentIdGenerator.execute(memberLeave.receiveCompanyId(), vacationId);

        assertThat(messages).isNotEmpty();
        assertThat(messages).extracting("body.requester_id").containsExactly(memberId);
        // 간헐적으로 ID 불일치, 이유 찾아야됨
        assertThat(messages).extracting("body.confirm_document_id").containsExactly(confirmDocumentId);
        assertThat(messages).extracting("body.confirm_status").containsExactly(ConfirmStatus.CREATE.name());
        assertThat(messages).extracting("body.approval_line_life_cycle").containsExactly(BEFORE_CREATE.name());
    }

    @DisplayName("차감 일 수가 잔여 연차일을 초과할 경우, VacationClientException 예외가 발생하고" +
            " 휴가 엔티티는 생성되지 않는다.")
    @Test
    void create_vacation_fail_use_leave_deduct_more_than_remaining_leave() {
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        RequestVacationForm vacationForm = new RequestVacationForm(
                memberLeave.getMemberId(), VacationType.MORE_DAY, LeaveDeduct.DEDUCT,
                List.of(new RequestVacationDuration(
                        LocalDateTime.of(2024, 3, 1, 0, 0),
                        LocalDateTime.of(2024, 3, 30, 0, 0))
                ), "휴가신청서",
                "개인사정",
                "이재헌",
                "위임자ID",
                "위임자 명",
                "테스트 부서 ID",
                "테스트 부서 명"
        );

        assertThatThrownBy(() -> vacationService.createVacation(vacationForm))
                .isInstanceOf(VacationClientException.class);
    }

    @DisplayName("휴가 업데이트 메서드 성공 케이스 " +
            "휴가 일자 수정 및 결재 서버로 메시지 전송")
    @Test
    void update_vacation_success() {
        //given
        /** 휴가 생성 START **/
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        // 금, 토
        RequestVacationForm vacationForm = new RequestVacationForm(
                memberLeave.getMemberId(), VacationType.MORE_DAY, LeaveDeduct.DEDUCT,
                List.of(new RequestVacationDuration(
                        LocalDateTime.of(2024, 3, 4, 0, 0),
                        LocalDateTime.of(2024, 3, 5, 0, 0))
                ), "휴가신청서",
                "개인사정",
                "이재헌",
                "위임자ID",
                "위임자명",
                "테스트 부서 ID",
                "테스트 부서 명"
        );

        VacationServiceResponse vacationResponse = vacationService.createVacation(vacationForm);
        VacationDurationDto vacationDurationDto = vacationResponse.vacationDuration().get(0);
        assertThat(vacationDurationDto.startDateTime())
                .isEqualTo(LocalDateTime.of(2024, 3, 4, 0, 0));
        assertThat(vacationDurationDto.endDateTime())
                .isEqualTo(LocalDateTime.of(2024, 3, 5, 0, 0));
        assertThat(vacationDurationDto.useLeaveValue())
                .isEqualTo(2);
        /** 휴가 생성 END **/

        // when
        /** 휴가 업데이트 START **/
        // 월, 화 -> 수, 목, 금 으로 변경
        UpdateVacationForm updateVacationForm = new UpdateVacationForm(
                "위임자ID", "위임자명", "사유",
                List.of(new RequestVacationDuration(
                        vacationDurationDto.vacationDurationId(),
                        LocalDateTime.of(2024, 3, 6, 0, 0),
                        LocalDateTime.of(2024, 3, 8, 0, 0))),
                0l);

        vacationService.updateVacation(vacationResponse.vacationId(), updateVacationForm);
        Vacation updatedVacation = vacationRepository.findById(vacationResponse.vacationId()).get();
        VacationDuration vacationDuration = updatedVacation.getVacationDurations().get(0);

        // then
        assertThat(vacationDuration.getStartDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 6, 0, 0));
        assertThat(vacationDuration.getEndDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 8, 0, 0));
        assertThat(vacationDuration.getUseLeaveValue()).isEqualTo(3);
        assertThat(vacationDuration.getLastDuration()).isEqualTo("Y");
    }

    @DisplayName("비활성화 된 사용자가 휴가를 신청할 경우," +
            "휴가는 생성되나 VacationStatus=FAIL 이다. " +
            "비활성화된 사용자가 휴가를 신청한다는 것 자체가 모순이기에 " +
            "요청자와 컨택하여 문제를 해결해야 하기에 데이터는 세이빙한다.")
    @Test
    void create_vacation_fail_inactive_member() {
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        memberLeave.retire();
        MemberLeave retireMember = memberLeaveRepository.save(memberLeave);

        RequestVacationForm vacationForm = new RequestVacationForm(
                memberLeave.getMemberId(), VacationType.MORE_DAY, LeaveDeduct.DEDUCT,
                List.of(new RequestVacationDuration(
                        LocalDateTime.of(2024, 3, 1, 0, 0),
                        LocalDateTime.of(2024, 3, 5, 0, 0))
                ), "휴가신청서",
                "개인사정",
                "이재헌",
                "위임자ID",
                "위임자 명",
                "테스트 부서 ID",
                "테스트 부서 명"
        );

        VacationServiceResponse response = vacationService.createVacation(vacationForm);

        assertThat(response.vacationStatus()).isEqualTo(VacationStatus.FAIL);
    }

    @DisplayName("휴가 상신 API flow " +
            "1. 상신이 가능한 문서인지 검증한다." +
            "2. 결재 서버로 상신 요청 보낸 후, 결과를 응답(결재 문서의 상태 등..)받는다." +
            "3. 결재 서버에서 받은 결재 문서의 상태가 RAISE 라면 휴가 상태를 REQUEST 로 변경한다." +
            "" +
            "2번의 경우 외부(결재 서버) API를 호출해야 하는데 테스트 환경에서는 이 부분을 새롭게 구현하여" +
            "API가 정상적으로 호출/응답됐다고 가정한다." +
            "이 부분은 BiFunction 타입의 람다로 구현되어 있다. ")
    @Test
    void raise_vacation_success_simple() {
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        RequestVacationForm vacationForm = new RequestVacationForm(
                memberLeave.getMemberId(), VacationType.MORE_DAY, LeaveDeduct.DEDUCT,
                List.of(new RequestVacationDuration(
                        LocalDateTime.of(2024, 3, 1, 0, 0),
                        LocalDateTime.of(2024, 3, 5, 0, 0))
                ), "휴가신청서",
                "개인사정",
                "이재헌",
                "위임자ID",
                "위임자 명",
                "테스트 부서 ID",
                "테스트 부서 명"
        );


        VacationServiceResponse vacation = vacationService.createVacation(vacationForm);

        //외부 서버와 통신하는 부분을 아래 람다로 대체 confirmStatus 필드를 RAISE 상태로 응답받아야 휴가의 상태가 변경된다.
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter =
                (v, m) -> new ConfirmDocumentRaiseResponse(execute(m.receiveCompanyId(), v.getId()), m.getMemberId(), "RAISE");
        // WHEN
        vacationService.raiseVacation(vacation.vacationId(), apiAdapter);

        // THEN
        Vacation updatedVacation = vacationRepository.findById(vacation.vacationId()).get();
        assertThat(updatedVacation.getVacationStatus()).isEqualTo(VacationStatus.REQUEST);
    }

    @DisplayName("비활성화 된 사용자가 휴가 상신을 할 경우, " +
            "생성된 휴가의 상태(VacationStatus)는 FAIL이다. ")
    @Test
    void raise_vacation_fail_member_inactive() {
        //given
        //비활성화 사용자 생성
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        memberLeave.retire(); // 사용자 비활성화
        memberLeaveRepository.save(memberLeave);
        //휴가 생성
        Vacation vacation = Vacation.builder()
                .leaveDeduct(LeaveDeduct.DEDUCT)
                .requesterId("T0001")
                .companyId("TJX")
                .vacationStatus(VacationStatus.CREATE)
                .vacationType(VacationType.MORE_DAY)
                .build();
        Vacation savedVacation = vacationRepository.save(vacation);

        // 외부 API 호출 대체
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter =
                (v, m) -> new ConfirmDocumentRaiseResponse(execute(m.receiveCompanyId(), v.getId()), m.getMemberId(), "RAISE");

        //when
        VacationServiceResponse response = vacationService.raiseVacationV2(savedVacation.getId(), apiAdapter);
        //then
        Vacation updatedVacation = vacationRepository.findById(savedVacation.getId()).get();
        assertThat(response.vacationStatus()).isEqualTo(VacationStatus.FAIL);
        assertThat(updatedVacation.getVacationStatus()).isEqualTo(VacationStatus.FAIL);
    }

    @DisplayName("Vacation 의 VacationStatus=CREATED 인 경우에만 상신이 가능하다." +
            "그외 상태에서 상신을 보낼 경우 " +
            "VacationClientException 예외가 발생한다.")
    @ParameterizedTest
    @EnumSource(names = {"REQUEST", "REJECT", "APPROVED", "CANCELED", "ONGOING", "COMPLETED", "FAIL", "ERROR"})
    void raise_vacation_fail_raise_impossible_status(VacationStatus vacationStatus) {
        //given
        //사용자 생성
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        memberLeaveRepository.save(memberLeave);
        //휴가 생성
        Vacation vacation = Vacation.builder()
                .leaveDeduct(LeaveDeduct.DEDUCT)
                .requesterId("T0001")
                .companyId("TJX")
                .vacationStatus(vacationStatus)
                .vacationType(VacationType.MORE_DAY)
                .build();
        Vacation savedVacation = vacationRepository.save(vacation);

        //when - then
        // 외부 API 호출 대체
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter =
                (v, m) -> new ConfirmDocumentRaiseResponse(execute(m.receiveCompanyId(), v.getId()), m.getMemberId(), "RAISE");

        assertThatThrownBy(() -> vacationService.raiseVacationV2(savedVacation.getId(), apiAdapter))
                .isInstanceOf(VacationClientException.class);
    }

    @DisplayName("외부 API 호출 시 정상 응답을 받을 수 없는 경우, " +
            "ServerCommunicationException 예외가 발생한다. " +
            "현재 케이스에서는 실제 결재 서버(외부) API 호출 구현체를 주입하였다.")
    @Test
    void raise_vacation_fail_external_api_throw_except() {
        //given
        //사용자 생성
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        memberLeaveRepository.save(memberLeave);
        //휴가 생성
        Vacation vacation = Vacation.builder()
                .leaveDeduct(LeaveDeduct.DEDUCT)
                .requesterId("T0001")
                .companyId("TJX")
                .vacationStatus(VacationStatus.CREATE)
                .vacationType(VacationType.MORE_DAY)
                .build();
        Vacation savedVacation = vacationRepository.save(vacation);

        //when - then
        /** 실제 외부 API 호출 구현체를 주입
         외부 API 서버는 shutdown 된 상태이기 때문에 ConnectException이 터지게 된다. **/
        ConfirmRaiseApiAdapter confirmRaiseApiAdapter = new ConfirmRaiseApiAdapter();

        assertThatThrownBy(() -> vacationService.raiseVacationV2(savedVacation.getId(), confirmRaiseApiAdapter))
                .isInstanceOf(ServerCommunicationException.class);
    }
}