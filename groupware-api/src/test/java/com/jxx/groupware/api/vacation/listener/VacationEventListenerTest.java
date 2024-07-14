package com.jxx.groupware.api.vacation.listener;

import com.jxx.groupware.core.message.infra.MessageQRepository;
import com.jxx.groupware.core.vacation.domain.entity.*;
import com.jxx.groupware.core.vacation.infra.MemberLeaveRepository;
import com.jxx.groupware.core.vacation.infra.OrganizationRepository;
import com.jxx.groupware.core.vacation.infra.VacationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class VacationEventListenerTest {

    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    MessageQRepository messageQRepository;
    @Autowired
    VacationRepository vacationRepository;
    @Autowired
    MemberLeaveRepository memberLeaveRepository;
    @Autowired
    OrganizationRepository organizationRepository;

    @AfterEach
    void afterEach() {
        vacationRepository.deleteAll();
        memberLeaveRepository.deleteAll();
        organizationRepository.deleteAll();
        messageQRepository.deleteAll();
    }

    @DisplayName("휴가 생성 이벤트 정상 호출 테스트")
    @Test
    void vacation_create_event_success() {
        //given
        MemberLeave memberLeave = MemberLeave.builder()
                .pk(100l)
                .memberId("U00100")
                .isActive(true)
                .experienceYears(2)
                .name("테스터")
                .leave(new Leave(15f, 15f))
                .enteredDate(LocalDate.of(2023, 5, 12))
                .organization(new Organization("JXX","JX사","J001", "IT팀", "J000", "상위부서"))
                .build();

        Vacation vacation = Vacation.builder()
                .vacationStatus(VacationStatus.CREATE)
                .leaveDeduct(LeaveDeduct.DEDUCT)
                .build();

        //when - then
        assertThatCode(() -> eventPublisher.publishEvent(new VacationCreatedEvent(
                memberLeave, vacation, 2f, "U00100", "DELEGATOR_ID", "DELEGATOR_NAME","개인사정")))
                .doesNotThrowAnyException();
    }

}