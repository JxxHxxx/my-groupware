package com.jxx.groupware.api.member.dto.request;

public record LoginRequest(
        String memberId,
        String password
) {
}
