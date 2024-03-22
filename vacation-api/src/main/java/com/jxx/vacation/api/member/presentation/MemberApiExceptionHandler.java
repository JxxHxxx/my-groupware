package com.jxx.vacation.api.member.presentation;

import com.jxx.vacation.api.member.dto.response.CommonResult;
import com.jxx.vacation.core.vacation.domain.exeception.AuthClientException;
import com.jxx.vacation.core.vacation.domain.exeception.MemberLeaveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.jxx.vacation.api.member"})
public class MemberApiExceptionHandler {

    @ExceptionHandler(UnAuthenticationException.class)
    public ResponseEntity<?> handleUnAuthenticationException(UnAuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(MemberLeaveException.class)
    public ResponseEntity<?> handleMemberLeaveException(MemberLeaveException exception) {
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
