package com.jxx.groupware.api.member.presentation;

import com.jxx.groupware.api.member.dto.response.CommonResult;
import com.jxx.groupware.api.member.listener.MemberOrgAuthenticationException;
import com.jxx.groupware.core.vacation.domain.exeception.AuthClientException;
import com.jxx.groupware.core.vacation.domain.exeception.MemberLeaveException;
import com.jxx.groupware.core.vacation.domain.exeception.OrganizationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.jxx.groupware.api.member", "com.jxx.groupware.api.work"})
public class MemberApiExceptionHandler {

    @ExceptionHandler({UnAuthenticationException.class, MemberOrgAuthenticationException.class})
    public ResponseEntity<?> handleUnAuthenticationException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CommonResult(HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), "No Contents"));
    }
    @ExceptionHandler({MemberLeaveException.class, OrganizationException.class})
    public ResponseEntity<?> handleMemberLeaveException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResult(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), "No Contents"));
    }
    @ExceptionHandler(AuthClientException.class)
    public ResponseEntity<?> handleAuthClientException(AuthClientException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResult(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), "No Contents"));
    }
}
