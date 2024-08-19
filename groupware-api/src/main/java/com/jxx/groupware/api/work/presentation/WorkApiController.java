package com.jxx.groupware.api.work.presentation;

import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.jxx.groupware.api.work.application.WorkService;
import com.jxx.groupware.api.work.dto.request.WorkTicketCreateRequest;
import com.jxx.groupware.api.work.dto.request.WorkTickSearchCond;
import com.jxx.groupware.api.work.dto.response.WorkTicketServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkApiController {

    private final WorkService workService;

    @PostMapping("/api/work-tickets")
    public ResponseEntity<?> createWorkTicket(@RequestBody WorkTicketCreateRequest WorkTicketCreateRequest) {
        WorkTicketServiceResponse response = workService.createWorkTicket(WorkTicketCreateRequest);
        return ResponseEntity.status(201).body(new ResponseResult<>(201, "작업 티켓 생성 완료", response));
    }

    @GetMapping("/api/work-tickets/search")
    public ResponseEntity<?> readWorkTicket(@ModelAttribute WorkTickSearchCond workTickSearchCond) {
        List<WorkTicketServiceResponse> response = workService.searchWorkTicket(workTickSearchCond);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 티켓 조회 완료", response));
    }

}
