package com.jxx.vacation.batch.presentation;

import com.jxx.vacation.core.common.response.HttpApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class JobApiExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        exception.getMessage();
        List<ObjectError> errors = exception.getBindingResult().getAllErrors();
        List<String> errMsgs = errors.stream().map(err -> err.getDefaultMessage()).toList();
        return ResponseEntity
                .badRequest()
                .body(new HttpApiResponse<>(400, new ErrMessage(errMsgs)));
    }

    @Getter
    @RequiredArgsConstructor
    class ErrMessage {
        private final List<String> errMessages;
    }
}
