package com.jxx.vacation.api.member.dto.response;


public record LoginResult<R>(
        int status,
        R data
){

}
