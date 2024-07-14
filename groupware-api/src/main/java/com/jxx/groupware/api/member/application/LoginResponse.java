package com.jxx.groupware.api.member.application;

public record LoginResponse(
        String companyId,
        String companyName,
        String memberId,
        String name,
        String departmentId,
        String departmentName
) {
}
