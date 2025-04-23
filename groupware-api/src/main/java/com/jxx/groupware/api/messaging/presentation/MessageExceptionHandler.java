package com.jxx.groupware.api.messaging.presentation;

import com.jxx.groupware.api.common.ExceptionResponseResult;
import com.jxx.groupware.api.messaging.application.MessageAdminException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {"com.jxx.groupware.api.messaging"})
public class MessageExceptionHandler {

    @ExceptionHandler(MessageAdminException.class)
    public ResponseEntity<?> handleMessageClientException(MessageAdminException exception) {
        log.info("exception {}", exception.toString());
        return ResponseEntity.badRequest().body(new ExceptionResponseResult(400, exception.toExceptionCommonResponse()));
    }
}
