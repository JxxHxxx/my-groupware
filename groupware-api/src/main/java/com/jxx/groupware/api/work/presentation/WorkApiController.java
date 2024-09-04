package com.jxx.groupware.api.work.presentation;

import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.jxx.groupware.api.work.application.WorkService;
import com.jxx.groupware.api.work.dto.request.*;
import com.jxx.groupware.api.work.dto.response.WorkDetailServiceResponse;
import com.jxx.groupware.api.work.dto.response.WorkServiceResponse;
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

    @PostMapping("/api/work-tickets/{work-ticket-id}/receive")
    public ResponseEntity<?> receiveWorkTicket(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketReceiveRequest request) {
        WorkDetailServiceResponse response = workService.receiveWorkTicketAndCreateWorkDetail(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(201, "작업 티켓 접수, 작업 내역 엔티티 생성", response));
    }

    @PatchMapping("/api/work-tickets/{work-ticket-id}/begin-analysis")
    public ResponseEntity<?> beginWorkDetailAnalysis(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketAnalyzeRequest request) {
        WorkServiceResponse response = workService.beginWorkDetailAnalysis(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 분석 단계 접수", response));
    }

    @PatchMapping("/api/work-tickets/{work-ticket-id}/complete-analysis")
    public ResponseEntity<?> completeWorkDetailAnalysis(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketAnalyzeRequest request) {
        WorkServiceResponse response = workService.completeWorkDetailAnalysis(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 분석 단계 완료", response));
    }

    @PatchMapping("/api/work-tickets/{work-ticket-id}/begin-plan")
    public ResponseEntity<?> beginWorkDetailPlan(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketPlanRequest request) {
        WorkServiceResponse response = workService.beginWorkDetailPlan(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 계획 단계 접수", response));
    }

    @PatchMapping("/api/work-tickets/{work-ticket-id}/complete-plan")
    public ResponseEntity<?> completeWorkDetailPlan(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketPlanRequest request) {
        WorkServiceResponse response = workService.completeWorkDetailPlan(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 계획 단계 완료", response));
    }

    @PatchMapping("/api/work-tickets/{work-ticket-id}/request-confirm")
    public ResponseEntity<?> requestConfirmWorkTicket(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketPlanRequest request) {
        workService.requestConfirm(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "결재 요청 완료", null));
    }
}
