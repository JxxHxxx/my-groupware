package com.jxx.vacation.batch.presentation;

import com.jxx.vacation.batch.domain.AdminClientException;
import com.jxx.vacation.batch.exception.JxxJobExecutionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class JobExecutionExceptionHandler {

    @ExceptionHandler(JxxJobExecutionException.class)
    public ResponseEntity<?> handle(JxxJobExecutionException exception) {
        return ResponseEntity.badRequest().body(exception.message());
    }

    @ExceptionHandler(JobInstanceAlreadyCompleteException.class)
    public ResponseEntity<?> handle(JobInstanceAlreadyCompleteException exception) {
        log.error("이미 완료된 잡 ID를 재실행하려고 합니다.", exception);
        return ResponseEntity.badRequest().body(new SimpleErrMsg("B01","이미 완료된 Job 인스턴스 입니다."));
    }

    @ExceptionHandler(AdminClientException.class)
    public ResponseEntity<?> handle(AdminClientException exception) {
        log.error("관리자 기능 사용중에 오류가 발생했습니다.");
        return ResponseEntity.badRequest().body((new SimpleErrMsg(exception.getErrCode(), exception.getMessage())));
    }

    @Getter
    @RequiredArgsConstructor
    private class SimpleErrMsg {
        private final String errCode;
        private final String message;
    }
}
