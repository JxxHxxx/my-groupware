package com.jxx.vacation.core.vacation.domain.entity;


import com.jxx.vacation.core.vacation.domain.exeception.VacationException;
import com.jxx.vacation.testUtil.CoreEntityFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class VacationManagerTest {

    // 인스턴스 생성 시, 가지는 특징
    @DisplayName("createVacation 정적 팩터리 메서드 사용 시, 인스턴스의 필드는 아래와 같은 특징을 가진다. " +
            "Vacation:신청자의 ID 값을 가진다.  " +
            "VacationDate:휴가 일수를 나타낸다. " +
            "MemberLeave:메서드 인자로 넣은 MemberLeave가 적용된다.")
    @Test
    void instantiate() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(2l);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);

        Organization organization = CoreEntityFactory.defalutOrganization();
        MemberLeave memberLeave = CoreEntityFactory.defaultMemberLeave(organization); // memberId == requesterId

        //when
        VacationManager vacationManager = VacationManager.createVacation(vacationDuration, memberLeave);

        System.out.println("is true? " + (memberLeave == vacationManager.getMemberLeave()));
        assertThat(vacationManager.getVacation()).extracting("requesterId").isEqualTo(memberLeave.getMemberId());
        assertThat(vacationManager.receiveVacationDate()).isEqualTo(3f);
        assertThat(vacationManager.getMemberLeave()).isEqualTo(memberLeave);
    }

    //
    @DisplayName("3113")
    @Test
    void isAlreadyVacationDate() {
        //given - 휴가 신청
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 3, 4, 0, 0);
        VacationDuration vacationDuration = new VacationDuration(VacationType.MORE_DAY, startDate, endDate);

        Organization organization = CoreEntityFactory.defalutOrganization();
        MemberLeave memberLeave = CoreEntityFactory.defaultMemberLeave(organization); // memberId == requesterId
        VacationManager vacationManager = VacationManager.createVacation(vacationDuration, memberLeave);

        // 기존에 존재하고 있는 아직 사용 전인 휴가
        LocalDateTime startDate2 = LocalDateTime.of(2024, 3, 2, 0, 0);
        LocalDateTime endDate2 = LocalDateTime.of(2024, 3, 3, 0, 0);
        VacationDuration vacationDuration2 = new VacationDuration(VacationType.MORE_DAY, startDate2, endDate2);

        Vacation existedVacation = new Vacation("T0001", vacationDuration2, true, VacationStatus.CREATE);

        //when - then
        assertThatCode(() -> vacationManager.validateVacationDatesAreDuplication(List.of(existedVacation)))
                .isInstanceOf(VacationException.class);

    }

    // 인스턴스 생성 후, 필드들의 비즈니스적 올바름 검증

    // 상신 가능 여부 확인

    // 상신

    // 취소 (결재 취소)

    // 수정
}