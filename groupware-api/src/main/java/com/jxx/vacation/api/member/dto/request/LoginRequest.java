package com.jxx.vacation.api.member.dto.request;

public record LoginRequest(
        String memberId,
        String password
) {
}
