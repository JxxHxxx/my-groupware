package com.jxx.vacation.api.vacation.application;


import com.jxx.vacation.api.vacation.dto.request.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.core.message.MessageQ;
import com.jxx.vacation.core.message.MessageQRepository;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.OrganizationRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
}