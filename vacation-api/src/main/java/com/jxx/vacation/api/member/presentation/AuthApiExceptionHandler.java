package com.jxx.vacation.api.member.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.jxx.vacation.api.member"})
public class AuthApiExceptionHandler {

    @ExceptionHandler(UnAuthenticationException.class)
    public ResponseEntity<?> handleUnAuthenticationException(UnAuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED);
    }

}
