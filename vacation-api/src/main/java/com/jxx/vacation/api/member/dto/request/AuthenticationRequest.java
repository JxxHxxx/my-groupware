package com.jxx.vacation.api.member.dto.request;

public record AuthenticationRequest(
        String memberId,
        String companyId,
        String departmentId,
        String name,
        String companyName,
        String departmentName
) {
}
