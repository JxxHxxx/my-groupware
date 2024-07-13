package com.jxx.vacation.api.member.dto.response;

import java.time.LocalDate;

public record MemberLeaveResponse(
        Long memberPk,
        String memberId,
        String name,
        Integer experienceYears,
        LocalDate enteredDate,
        Float totalLeave,
        Float remainingLeave,
        String companyId,
        String companyName,
        String departmentId,
        String departmentName
        ) {

}
