package com.jxx.groupware.api.work.presentation;


import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.jxx.groupware.core.work.domain.exception.WorkClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {"com.jxx.groupware.api.work"})
public class WorkApiExceptionHandler {

    @ExceptionHandler(WorkClientException.class)
    public ResponseEntity<?> handleWorkClientException(WorkClientException exception) {
        log.info("{}",exception.toString(), exception);

        return ResponseEntity
                .badRequest()
                .body(new ResponseResult<>(400, exception.getMessage(), exception.getErrCode()));
    }
}
