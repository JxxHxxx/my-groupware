package com.jxx.groupware.api.vacation.presentation;

import com.jxx.groupware.api.common.web.ServerCommunicationException;
import com.jxx.groupware.api.excel.ExcelFileReadException;
import com.jxx.groupware.api.member.presentation.UnAuthenticationException;
import com.jxx.groupware.api.vacation.dto.response.ClientExceptionResponse;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.jxx.groupware.api.member.presentation.AuthorizationException;
import com.jxx.groupware.core.vacation.domain.exeception.VacationAdminException;
import com.jxx.groupware.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;
import java.util.List;

@Slf4j
@RestControllerAdvice(basePackages = {"com.jxx.groupware.api.vacation"})
public class VacationApiExceptionHandler {

    @ExceptionHandler(UnAuthenticationException.class)
    public ResponseEntity<?> handleUnAuthenticationException(UnAuthenticationException exception) {
        ClientExceptionResponse response = new ClientExceptionResponse(401, null, "잘못된 접근입니다.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(VacationClientException.class)
    public ResponseEntity<?> handleVacationException(VacationClientException exception) {
        log.error("[{}][{}]", exception.getClientId(), exception.getMessage(), exception);
        ClientExceptionResponse response = new ClientExceptionResponse(400, exception.getClientId(), exception.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ServerCommunicationException.class)
    public ResponseEntity<?> handleServerServiceException(ServerCommunicationException exception) {
        Integer statusCode = exception.getStatusCode();
        return ResponseEntity.status(statusCode)
                .body(new ResponseResult<>(statusCode, exception.getMessage(), null));
    }

    @ExceptionHandler({ConnectException.class, RestClientException.class})
    public ResponseEntity<?> handleServerServiceException(ConnectException exception) {
        return ResponseEntity.internalServerError()
                .body(new ResponseResult<>(500, exception.getMessage(), null));

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> errMsgs = exception.getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .toList();

        String errMsg = "";
        if (!errMsgs.isEmpty()) {
            errMsg = errMsgs.get(0);
        }

        // 총 예외 메시지 수 표시 errMsgs.size()
        return ResponseEntity.badRequest()
                .body(new ResponseResult<>(400, errMsg, errMsgs.size()));
    }

    @ExceptionHandler(ExcelFileReadException.class)
    public ResponseEntity<?> handleExcelFileReadException(ExcelFileReadException exception) {
        log.error("[{}][{}]", exception.purpose(), exception.getMessage());
        return ResponseEntity.badRequest()
                .body(new ResponseResult<>(400, exception.getMessage(), exception.purpose()));
    }

    @ExceptionHandler(VacationAdminException.class)
    public ResponseEntity<?> handleVacationAdminException(VacationAdminException exception) {
        return ResponseEntity.badRequest()
                .body(new ResponseResult<>(400, exception.getMessage(), null));

    }

//    @ExceptionHandler(AuthorizationException.class)
//    public ResponseEntity<?> handleAuthorizationException(AuthorizationException exception) {
//        String requesterId = exception.getRequesterId();
//        return ResponseEntity.status(403)
//                .body(new ResponseResult<>(403, exception.getMessage(), requesterId));
//
//    }
}
