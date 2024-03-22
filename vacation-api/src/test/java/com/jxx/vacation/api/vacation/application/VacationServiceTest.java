package com.jxx.vacation.api.vacation.application;


import com.jxx.vacation.api.vacation.dto.request.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.vacation.core.message.MessageQ;
import com.jxx.vacation.core.message.MessageQRepository;
import com.jxx.vacation.core.message.payload.approval.ConfirmStatus;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.OrganizationRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.*;
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


    @DisplayName("휴가 상신 API flow " +
            "1. 상신이 가능한 문서인지 검증한다." +
            "2. 결재 서버로 상신 요청 보낸 후, 결과를 응답(결재 문서의 상태 등..)받는다." +
            "3. 결재 서버에서 받은 결재 문서의 상태가 RAISE 라면 휴가 상태를 REQUEST 로 변경한다." +
            "" +
            "2번의 경우 외부(결재 서버) API를 호출해야 하는데 테스트 환경에서는 이 부분을 새롭게 구현하여" +
            "API가 정상적으로 호출/응답됐다고 가정한다." +
            "이 부분은 BiFunction 타입의 람다로 구현되어 있다. ")
    @Test
    void raise_vacation() {
        String memberId = "T0001";
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(memberId).get();
        RequestVacationForm vacationForm = new RequestVacationForm(
                memberLeave.getMemberId(), new VacationDuration(VacationType.MORE_DAY,
                LocalDateTime.of(9999, 3, 1, 0, 0),
                LocalDateTime.of(9999, 3, 4, 0, 0)));


        VacationServiceResponse vacation = vacationService.createVacation(vacationForm);

        //외부 서버와 통신하는 부분을 아래 람다로 대체
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter =
                (v, m) -> new ConfirmDocumentRaiseResponse(execute(m.receiveCompanyId(), v.getId()), m.getMemberId(), "RAISE");

        // WHEN
        vacationService.raiseVacation(vacation.vacationId(), apiAdapter);

        // THEN
        Vacation updatedVacation = vacationRepository.findById(vacation.vacationId()).get();
        assertThat(updatedVacation.getVacationStatus()).isEqualTo(VacationStatus.REQUEST);

    }
}