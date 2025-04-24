package com.jxx.groupware.api.common.exception;


import com.jxx.groupware.api.member.presentation.UnAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.jxx.groupware.api")
public class AuthExceptionHandler {

    @ExceptionHandler(UnAuthenticationException.class)
    public ResponseEntity<?> handleUnAuthenticationException(UnAuthenticationException exception) {
        ExceptionCommonResponse response = exception.toExceptionCommonResponse();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
}
