package com.jxx.vacation.api.vacation.presentation;

import com.jxx.vacation.api.common.web.ServerServiceException;
import com.jxx.vacation.api.vacation.dto.response.ClientExceptionResponse;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {"com.jxx.vacation.api.vacation"})
public class VacationApiExceptionHandler {

    @ExceptionHandler(VacationClientException.class)
    public ResponseEntity<?> handleVacationException(VacationClientException exception) {
        log.error("[{}][{}]", exception.getClientId(), exception.getMessage());
        ClientExceptionResponse response = new ClientExceptionResponse(400, exception.getClientId(), exception.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ServerServiceException.class)
    public ResponseEntity<?> handleServerServiceException(ServerServiceException exception) {
        return ResponseEntity.internalServerError().body(new ResponseResult(500,  exception.getMessage(), exception.getRequestUri()));
    }
}
