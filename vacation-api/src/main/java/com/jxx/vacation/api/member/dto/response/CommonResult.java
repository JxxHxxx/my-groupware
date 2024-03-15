package com.jxx.vacation.api.member.dto.response;

public record CommonResult<R> (
        int status,
        String message,
        R data
){
}
