package com.jxx.groupware.api.member.dto.response;


public record LoginResult<R>(
        int status,
        R data
){

}
