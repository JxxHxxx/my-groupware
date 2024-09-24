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

    /**
     * 작업 티켓 생성 API
     */
    @PostMapping("/api/work-tickets")
    public ResponseEntity<?> createWorkTicket(@RequestBody WorkTicketCreateRequest WorkTicketCreateRequest) {
        WorkTicketServiceResponse response = workService.createWorkTicket(WorkTicketCreateRequest);
        return ResponseEntity.status(201).body(new ResponseResult<>(201, "작업 티켓 생성 완료", response));
    }

    /**
     * 작업 티켓 By workTicketPk 조회 API
     */
    @GetMapping("/api/work-tickets/{work-ticket-pk}")
    public ResponseEntity<?> getWorkTicketByPk(@PathVariable("work-ticket-pk") Long workTicketPk) {
        WorkServiceResponse response = workService.getWorkTicketByPk(workTicketPk);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 티켓 PK:" + workTicketPk + " + 조회", response));
    }

    /**
     * 작업 티켓 검색 API
     *
     * @param workTickSearchCond : 검색 조건
     */
    @GetMapping("/api/work-tickets/search")
    public ResponseEntity<?> searchWorkTicket(@ModelAttribute WorkTickSearchCond workTickSearchCond) {
        List<WorkTicketServiceResponse> response = workService.searchWorkTicket(workTickSearchCond);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 티켓 조회 완료", response));
    }

    /**
     * 작업 티켓 접수 API
     * 작업 티켓 접수 성공 시, 작업 내역(WorkDetail) 엔티티 생성
     */
    @PostMapping("/api/work-tickets/{work-ticket-id}/receive")
    public ResponseEntity<?> receiveWorkTicket(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketReceiveRequest request) {
        WorkDetailServiceResponse response = workService.receiveWorkTicketAndCreateWorkDetail(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(201, "작업 티켓 접수, 작업 내역 엔티티 생성", response));
    }

    /**
     * 작업 분석 단계 접수 API
     */
    @PatchMapping("/api/work-tickets/{work-ticket-id}/begin-analysis")
    public ResponseEntity<?> beginWorkDetailAnalysis(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketAnalyzeRequest request) {
        WorkServiceResponse response = workService.beginWorkDetailAnalysis(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 분석 단계 접수", response));
    }

    /**
     * 작업 분석 단계 완료 API
     */
    @PatchMapping("/api/work-tickets/{work-ticket-id}/complete-analysis")
    public ResponseEntity<?> completeWorkDetailAnalysis(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketAnalyzeRequest request) {
        WorkServiceResponse response = workService.completeWorkDetailAnalysis(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 분석 단계 완료", response));
    }

    /**
     * 작업 계획 단계 접수 API
     */
    @PatchMapping("/api/work-tickets/{work-ticket-id}/begin-plan")
    public ResponseEntity<?> beginWorkDetailPlan(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketPlanRequest request) {
        WorkServiceResponse response = workService.beginWorkDetailPlan(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 계획 단계 접수", response));
    }

    /**
     * 작업 계획 단계 완료 API
     */
    @PatchMapping("/api/work-tickets/{work-ticket-id}/complete-plan")
    public ResponseEntity<?> completeWorkDetailPlan(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketPlanRequest request) {
        WorkServiceResponse response = workService.completeWorkDetailPlan(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 계획 단계 완료", response));
    }

    /**
     * 결재 요청 API
     */
    @PatchMapping("/api/work-tickets/{work-ticket-id}/request-confirm")
    public ResponseEntity<?> requestConfirmWorkTicket(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketPlanRequest request) {
        workService.requestConfirm(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "결재 요청 성공", null));
    }

    /**
     * <pre> MAKE FOR THIRD PARTY
     * 결재 문서가 최종 결정(최종 승인/반려)되었을 때
     * 작업 티켓 도메인에 필요한 후속 처리를 하는 API
     * 최종 결정은 결재 서버 WAS에서 판단 가능하기에 이 API는 결재 서버에서 호출한다.
     * </pre>
     *
     * @Param workTicketPk - 작업 티켓 PK(ID 와 다른 값이니 혼동 X)
     **/
    @PatchMapping("/api/work-tickets/{work-ticket-pk}/complete-confirm")
    public ResponseEntity<?> requestConfirmWorkTicket(@PathVariable("work-ticket-pk") Long workTicketPk, @RequestBody WorkTicketCompleteConfirmRequest request) {
        workService.processingAfterConfirmComplete(workTicketPk, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "결재 최종 결정 후 후속 처리", null));
    }
}
