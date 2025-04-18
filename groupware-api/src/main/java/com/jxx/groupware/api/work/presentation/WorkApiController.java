package com.jxx.groupware.api.work.presentation;

import com.jxx.groupware.api.file.domain.StorageService;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.jxx.groupware.api.work.application.WorkService;
import com.jxx.groupware.api.work.dto.request.*;
import com.jxx.groupware.api.work.dto.response.WorkDetailServiceResponse;
import com.jxx.groupware.api.work.dto.response.WorkServiceResponse;
import com.jxx.groupware.api.work.dto.response.WorkTicketSearchResponse;
import com.jxx.groupware.api.work.dto.response.WorkTicketServiceResponse;
import com.jxx.groupware.core.work.domain.WorkTicketAttachment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class WorkApiController {

    private final WorkService workService;
    private final StorageService storageService;

    /**
     * 작업 티켓 생성 API
     */
    @PostMapping("/api/work-tickets")
    public ResponseEntity<?> createWorkTicket(@RequestBody WorkTicketCreateRequest WorkTicketCreateRequest) {
        WorkTicketServiceResponse response = workService.createWorkTicket(WorkTicketCreateRequest);
        return ResponseEntity.status(201).body(new ResponseResult<>(201, "작업 티켓 생성 완료", response));
    }

    @PostMapping("/api/work-ticket-attachments")
    public ResponseEntity<?> createWorkTicket(@RequestParam("file") MultipartFile file, @RequestParam String workTicketId) throws IOException {
        String encodeUrl = storageService.store(file);
        workService.saveAttachment(workTicketId, encodeUrl);
        return ResponseEntity.status(201).body(new ResponseResult<>(201, "작업 티켓 첨부 파일 저장 완료", null));
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
    public ResponseEntity<?> searchWorkTicket(@ModelAttribute WorkTickSearchCond workTickSearchCond,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "30") int size) {
        PageImpl<WorkTicketSearchResponse> response = workService.searchWorkTicket(workTickSearchCond, page, size);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 티켓 조회 성공", response));
    }

    /**
     * 작업 티켓 삭제 API - soft delete
     */
    @DeleteMapping("/api/work-tickets/{work-ticket-id}")
    public ResponseEntity<?> deleteWorkTicket(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketDeleteRequest request) {
        WorkTicketServiceResponse response = workService.deleteWorkTicket(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 티켓 삭제 성공", response));
    }

    /**
     * 작업 티켓 반려(접수자의 반려) API
     * info) 요청자의 경우, 접수자가 접수하기 전에 삭제 가능하며
     * 결재 진행 중에 반려를 통해 반려 가능하다.
     */
    @PatchMapping("/api/work-tickets/{work-ticket-id}/reject-from-receiver")
    public ResponseEntity<?> rejectWorkTicketFromReceiver(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketRejectRequest request) {
        WorkServiceResponse response = workService.rejectWorkTicketFromReceiver(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 티켓 반려 성공", response));
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
    public ResponseEntity<?> requestConfirmForWorkTicket(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketPlanRequest request) {
        workService.requestConfirmForWorkTicket(workTicketId, request);
        return ResponseEntity.accepted().body(new ResponseResult<>(202, "결재 요청 성공", null));
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
    public ResponseEntity<?> processingAfterConfirmComplete(@PathVariable("work-ticket-pk") Long workTicketPk, @RequestBody WorkTicketCompleteConfirmRequest request) {
        workService.processingAfterConfirmComplete(workTicketPk, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "결재 최종 결정 후 후속 처리", null));
    }

    /** 작업 시작 API **/
    @PatchMapping("/api/work-tickets/{work-ticket-id}/begin-work")
    public ResponseEntity<?> beginWork(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketBeginWorkRequest request) {
        WorkServiceResponse response = workService.beginWork(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 시작", response));
    }

    /** 작업 종료 API **/
    @PatchMapping("/api/work-tickets/{work-ticket-id}/complete-work")
    public ResponseEntity<?> completeWork(@PathVariable("work-ticket-id") String workTicketId, @RequestBody WorkTicketCompleteRequest request) {
        WorkServiceResponse response = workService.completeWork(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "작업 완료", response));
    }

    /** 작업 선처리 **/
    @PatchMapping("/api/work-tickets/{work-ticket-id}/pre-reflect")
    public ResponseEntity<?> preReflect(@PathVariable("work-ticket-id") String workTicketId, @RequestBody Object request) {
        WorkServiceResponse response = workService.preReflect(workTicketId, request);
        return ResponseEntity.ok(new ResponseResult<>(200, "선처리 시작", response));

    }
}
