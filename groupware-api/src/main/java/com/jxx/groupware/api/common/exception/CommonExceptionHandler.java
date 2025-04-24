package com.jxx.groupware.api.common.exception;


import com.jxx.groupware.api.member.presentation.UnAuthenticationException;
import com.jxx.groupware.api.member.presentation.AuthorizationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.jxx.groupware.api")
public class CommonExceptionHandler {

    @ExceptionHandler(UnAuthenticationException.class)
    public ResponseEntity<ExceptionResponseResult> handleUnAuthenticationException(UnAuthenticationException exception) {
        ExceptionCommonResponse response = exception.toExceptionCommonResponse();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponseResult<ExceptionCommonResponse>(401,  response));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponseResult> handleAuthorizationException(AuthorizationException exception) {
        ExceptionCommonResponse response = exception.toExceptionCommonResponse();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponseResult<ExceptionCommonResponse>(403,  response));
    }

    // 스프링 Valid - Validation 애노테이션 사용 시 처리해주는 부분 400 HttpStatusCode 로 정상 응답 할 수 있도록 변경
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseResult> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> defaultErrorMessages = exception.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ExceptionCommonResponse response = new ExceptionCommonResponse(ErrorCode.COM_API_F_001.getErrorCode(), defaultErrorMessages.toString());
        return ResponseEntity.badRequest()
                .body(new ExceptionResponseResult<ExceptionCommonResponse>(400, response));
    }
}
