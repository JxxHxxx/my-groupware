package com.jxx.groupware.testUtil;

import com.jxx.groupware.core.vacation.domain.entity.Leave;
import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.domain.entity.Organization;

import java.time.LocalDate;

public class CoreEntityFactory {

    public static Organization defalutOrganization() {
        return new Organization(
                "O0001",
                "TJX",
                "T0001",
                "테스트부서",
                "T0000",
                "상위부서");

    }
    public static MemberLeave defaultMemberLeave(Organization organization) {
        return MemberLeave.builder()
                .memberId("T0001")
                .isActive(true)
                .name("나재헌")
                .experienceYears(1)
                .enteredDate(LocalDate.of(2023, 8, 16))
                .leave(new Leave(15F, 15F))
                .organization(organization)
                .build();
    }
}
