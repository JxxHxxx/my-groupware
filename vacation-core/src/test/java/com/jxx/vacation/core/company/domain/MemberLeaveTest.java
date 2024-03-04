package com.jxx.vacation.core.company.domain;

import com.jxx.vacation.core.vacation.domain.entity.Leave;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.domain.exeception.MemberLeaveException;
import com.jxx.vacation.testUtil.CoreEntityFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class MemberLeaveTest {

    @DisplayName("1.신청 휴가 일이 잔여 연차보다 많은 경우" +
                 "2.신청 휴가 일이 0 미만 일 경우 " +
            "아래 메서드를 호출 할 시 MemberLeaveException 예외가 발생한다.")
    @ValueSource(floats = {16f, -0.5f})
    @ParameterizedTest
    void checkRemainingLeaveBiggerThan(float requestVacationDate) {
        LocalDate enterDate = LocalDate.of(2023, 8, 16);
        Organization organization = CoreEntityFactory.defalutOrganization();

        MemberLeave memberLeave = MemberLeave.builder()
                .memberId("T0001")
                .name("나재헌")
                .experienceYears(1)
                .enteredDate(enterDate)
                .leave(new Leave(15f, 15f))
                .organization(organization)
                .build();

        assertThatThrownBy(() -> memberLeave.checkRemainingLeaveBiggerThan(requestVacationDate))
                .isInstanceOf(MemberLeaveException.class);
    }

}