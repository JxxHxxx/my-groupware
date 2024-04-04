package com.jxx.vacation.batch.presentation;

import com.jxx.vacation.batch.exception.JxxJobExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JobExecutionExceptionHandler {

    @ExceptionHandler(JxxJobExecutionException.class)
    public ResponseEntity<?> handle(JxxJobExecutionException exception) {
        return ResponseEntity.badRequest().body(exception.message());
    }
}
