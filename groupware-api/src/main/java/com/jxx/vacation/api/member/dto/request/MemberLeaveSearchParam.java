package com.jxx.vacation.api.member.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberLeaveSearchParam {
    private final String companyId;
    private final String departmentId;

}
