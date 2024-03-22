package com.jxx.vacation.api.vacation.application;


import com.jxx.vacation.api.vacation.dto.request.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.vacation.core.message.MessageQ;
import com.jxx.vacation.core.message.MessageQRepository;
import com.jxx.vacation.core.message.payload.approval.ConfirmStatus;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.OrganizationRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import static com.jxx.vacation.core.common.generator.ConfirmDocumentIdGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
class VacationServiceTest {

    @Autowired
    VacationService vacationService;
    @Autowired
    VacationRepository vacationRepository;
    @Autowired
    MemberLeaveRepository memberLeaveRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    MessageQRepository messageQRepository;

    @BeforeEach
    void beforeEach() {
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
    }

    @Test
    void create_vacation_success_case() {
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        RequestVacationForm vacationForm = new RequestVacationForm(
                memberLeave.getMemberId(), new VacationDuration(VacationType.MORE_DAY,
                LocalDateTime.of(2024, 3, 1, 0, 0),
                LocalDateTime.of(2024, 3, 4, 0, 0)));

        // WHEN
        vacationService.createVacation(vacationForm);

        // 비동기 task 호출 확인
        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            List<MessageQ> messages = messageQRepository.findWithLimit(1);
            return !messages.isEmpty(); // 비어있지 않으면 호출되었단느 의미임
        });

        // THEN
        List<VacationServiceResponse> responses = vacationService.readByRequesterId(memberId);
        assertThat(responses).extracting("vacationDuration.startDateTime")
                .contains(LocalDateTime.of(2024, 3, 1, 0, 0));
        assertThat(responses).extracting("vacationDuration.endDateTime")
                .contains(LocalDateTime.of(2024, 3, 4, 0, 0));

        List<MessageQ> messages = messageQRepository.findWithLimit(1);
        assertThat(messages).isNotEmpty();
        assertThat(messages).extracting("body.requester_id").contains(memberId);
    }

    @DisplayName("휴가 신청이 되지 않을 경우,")
    @Test
    void create_vacation_success_fail() {
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        RequestVacationForm vacationForm = new RequestVacationForm(
                memberLeave.getMemberId(), new VacationDuration(VacationType.MORE_DAY,
                LocalDateTime.of(2024, 3, 1, 0, 0),
                LocalDateTime.of(2024, 3, 17, 0, 0)));

        assertThatThrownBy(() -> vacationService.createVacation(vacationForm))
                .isInstanceOf(VacationClientException.class);
    }

    @DisplayName("비활성화 된 사용자가 휴가를 신청할 경우," +
            "휴가는 생성되나 VacationStatus=FAIL 이다. " +
            "비활성화된 사용자가 휴가를 신청한다는 것 자체가 모순이기에 " +
            "요청자와 컨택하여 문제를 해결해야 하기에 데이터는 세이빙한다.")
    @Test
    void create_vacation_success_fail_inactive_member() {
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        memberLeave.retire();
        MemberLeave retireMember = memberLeaveRepository.save(memberLeave);

        RequestVacationForm vacationForm = new RequestVacationForm(
                retireMember.getMemberId(), new VacationDuration(VacationType.MORE_DAY,
                LocalDateTime.of(9999, 3, 1, 0, 0),
                LocalDateTime.of(9999, 3, 4, 0, 0)));

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
                memberLeave.getMemberId(), new VacationDuration(VacationType.MORE_DAY,
                LocalDateTime.of(9999, 3, 1, 0, 0),
                LocalDateTime.of(9999, 3, 4, 0, 0)));


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
                .vacationDuration(new VacationDuration(VacationType.MORE_DAY,
                        LocalDateTime.of(9999, 3, 1, 0, 0),
                        LocalDateTime.of(9999, 3, 4, 0, 0)))
                .deducted(true)
                .requesterId("T0001")
                .vacationStatus(VacationStatus.CREATE)
                .build();
        Vacation savedVacation = vacationRepository.save(vacation);

        // 외부 API 호출 대체
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter =
                (v, m) -> new ConfirmDocumentRaiseResponse(execute(m.receiveCompanyId(), v.getId()), m.getMemberId(), "RAISE");

        //when
        VacationServiceResponse response = vacationService.raiseVacation(savedVacation.getId(), apiAdapter);
        //then
        Vacation updatedVacation = vacationRepository.findById(savedVacation.getId()).get();
        assertThat(response.vacationStatus()).isEqualTo(VacationStatus.FAIL);
        assertThat(updatedVacation.getVacationStatus()).isEqualTo(VacationStatus.FAIL);
    }

    @DisplayName("Vacation 의 VacationStatus=CREATED 인 경우에만 상신이 가능하다." +
            "그외 상태에서 상신을 보낼 경우 " +
            "VacationClientException 예외가 발생한다.")
    @ParameterizedTest
    @EnumSource(names = {"REQUEST", "REJECT", "APPROVED", "CANCELED","ONGOING", "COMPLETED", "FAIL", "ERROR"})
    void raise_vacation_fail_raise_impossible_status(VacationStatus vacationStatus) {
        //given
        //사용자 생성
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        memberLeaveRepository.save(memberLeave);
        //휴가 생성
        Vacation vacation = Vacation.builder()
                .vacationDuration(new VacationDuration(VacationType.MORE_DAY,
                        LocalDateTime.of(9999, 3, 1, 0, 0),
                        LocalDateTime.of(9999, 3, 4, 0, 0)))
                .deducted(true)
                .requesterId("T0001")
                .vacationStatus(vacationStatus)
                .build();
        Vacation savedVacation = vacationRepository.save(vacation);

        //when - then
        // 외부 API 호출 대체
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter =
                (v, m) -> new ConfirmDocumentRaiseResponse(execute(m.receiveCompanyId(), v.getId()), m.getMemberId(), "RAISE");

        assertThatThrownBy(() -> vacationService.raiseVacation(savedVacation.getId(), apiAdapter))
                .isInstanceOf(VacationClientException.class);
    }
}