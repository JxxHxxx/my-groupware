package com.jxx.groupware.api.work.presentation;

import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.jxx.groupware.api.work.application.WorkService;
import com.jxx.groupware.api.work.application.WorkTicketCreateRequest;
import com.jxx.groupware.api.work.dto.response.WorkTicketCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkApiController {

    private final WorkService workService;

    @PostMapping("/api/work-tickets")
    public ResponseEntity<?> createWorkTicket(@RequestBody WorkTicketCreateRequest WorkTicketCreateRequest) {
        WorkTicketCreateResponse response = workService.createWorkTicket(WorkTicketCreateRequest);
        return ResponseEntity.status(201).body(new ResponseResult<>(201, "작업 티켓 생성 완료", response));
    }
}
