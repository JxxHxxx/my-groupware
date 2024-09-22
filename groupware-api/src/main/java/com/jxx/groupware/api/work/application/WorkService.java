package com.jxx.groupware.api.work.application;

import com.jxx.groupware.core.ConfirmCreateForm;
import com.jxx.groupware.api.work.dto.request.*;
import com.jxx.groupware.api.work.dto.response.WorkDetailServiceResponse;
import com.jxx.groupware.api.work.dto.response.WorkServiceResponse;
import com.jxx.groupware.api.work.dto.response.WorkTicketServiceResponse;
import com.jxx.groupware.api.work.listener.RestApiRequestEvent;
import com.jxx.groupware.api.work.query.WorkTicketMapper;
import com.jxx.groupware.core.message.body.vendor.confirm.ConfirmStatus;
import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.infra.MemberLeaveRepository;
import com.jxx.groupware.core.work.domain.*;
import com.jxx.groupware.core.work.domain.exception.WorkClientException;
import com.jxx.groupware.core.work.infra.WorkDetailRepository;
import com.jxx.groupware.core.work.infra.WorkTicketAttachmentRepository;
import com.jxx.groupware.core.work.infra.WorkTicketHistRepository;
import com.jxx.groupware.core.work.infra.WorkTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkTicketRepository workTicketRepository;
    private final WorkTicketHistRepository workTicketHistRepository;
    private final WorkTicketMapper workTicketMapper;
    private final WorkDetailRepository workDetailRepository;
    private final WorkTicketAttachmentRepository workTicketAttachmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final MemberLeaveRepository memberLeaveRepository; // 임시

    /**
     * 작업 티켓 생성
     **/
    @Transactional
    public WorkTicketServiceResponse createWorkTicket(final WorkTicketCreateRequest request) {
        // 요청자가 다른 회사에 요청을 하려는 경우 제외
        if (!Objects.equals(request.workRequester().getCompanyId(), request.chargeCompanyId())) {
            throw new WorkClientException("다른 회사에 요청을 할 수 없습니다.");
        }
        /** 타 도메인 Event 처리
         * **/
        eventPublisher.publishEvent(new WorkTicketCreateEvent(request.chargeCompanyId(), request.chargeDepartmentId()));

        WorkTicket workTicket = WorkTicket.builder()
                .workStatus(WorkStatus.CREATE)
                .createdTime(LocalDateTime.now())
                .chargeCompanyId(request.chargeCompanyId())
                .chargeDepartmentId(request.chargeDepartmentId())
                .modifiedTime(LocalDateTime.now())
                .requestTitle(request.requestTitle())
                .requestContent(request.requestContent())
                .workRequester(request.workRequester())
                .build();

        WorkTicket savedWorkTicket = workTicketRepository.save(workTicket);
        log.info("success save workTicket {}", savedWorkTicket.getWorkTicketId());

        WorkTicketHistory workTicketHistory = createWorkTicketHistory(request, savedWorkTicket);

        WorkTicketHistory savedWorkTicketHistory = workTicketHistRepository.save(workTicketHistory);
        log.info("success save workTicket history {}", savedWorkTicketHistory.getWorkTicketId());

        return createWorkTicketServiceResponse(savedWorkTicket);
    }
    /** 작업 티켓 PK 조회**/
    public WorkServiceResponse getWorkTicketByPk(Long workTicketPk) {
        WorkTicket workTicket = workTicketRepository.findById(workTicketPk)
                .orElseThrow(() -> new WorkClientException(workTicketPk + "에 해당하는 WorkTicket은 존재 않습니다."));

        /** 작업 티켓이 CREATE 상태일 때는 WorkDetail 이 존재하지 않기 때문에 분기 처리**/
        if (Objects.equals(workTicket.getWorkStatus(), WorkStatus.CREATE)) {

            WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
            return new WorkServiceResponse(workTicketServiceResponse, null);
        }
        else  {
            WorkDetail workDetail = workTicket.getWorkDetail();
            WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
            WorkDetailServiceResponse workDetailServiceResponse = createWorkDetailServiceResponse(workDetail);
            return new WorkServiceResponse(workTicketServiceResponse, workDetailServiceResponse);
        }
    }

    /** 작업 티켓 삭제
     * workStatus -> DELETE **/
    @Transactional
    public void deleteWorkTicket() {

    }

    /** 작업 반려
     * workStatus -> REJECT_FROM_CHARGE **/
    @Transactional
    public void rejectWorkTicket() {

    }

    /**
     * 작업 티켓 검색
     **/
    public List<WorkTicketServiceResponse> searchWorkTicket(WorkTickSearchCond searchCond) {
        return workTicketMapper.search(searchCond);

    }

    /**
     * 작업 티켓 접수 : 접수에서 WorkDetail 만들어짐
     **/
    @Transactional
    public WorkDetailServiceResponse receiveWorkTicketAndCreateWorkDetail(final String workTicketId, WorkTicketReceiveRequest request) {
        WorkTicket workTicket = workTicketRepository.findByWorkTicketId(workTicketId)
                .orElseThrow(() -> new WorkClientException("workTicket workTicketId:" + workTicketId + " 는 존재하지 않습니다."));

        /** 작업 티켓이 접수 가능한지 검증 **/
        if (workTicket.isNotReceivable()) {
            log.error("\n workTicketId:{} is already received or can't receive \n " +
                    "now workStatus is {}", workTicket.getWorkTicketId(), workTicket.getWorkStatus());
            throw new WorkClientException("티켓:" + workTicketId + "이미 접수되었거나 접수할 수 없는 상태입니다.");
        }

        /** 타 도메인 Event 처리
         * 접수자가 요청 대상 부서의 소속인지 검증 **/
        eventPublisher.publishEvent(
                new WorkTicketReceiveEvent(
                        request.receiverId(),
                        request.receiverCompanyId(),
                        request.receiverDepartmentId(),
                        workTicket.getChargeCompanyId(),
                        workTicket.getChargeDepartmentId()));

        /** 작업 상세(작업 접수자의 작업 검토, 계획, 수행 데이터가 들어감) 엔티티 생성 **/
        WorkDetail workDetail = WorkDetail.builder()
                .receiverId(request.receiverId())
                .receiverName(request.receiverName())
                .createTime(LocalDateTime.now())
                .build();
        WorkDetail savedWorkDetail = workDetailRepository.save(workDetail);

        /** 작업이 접수됐으면 작업 티켓 상태를 변경해야함
         * + JPA Dirty Checking **/
        workTicket.changeWorkStatus(WorkStatus.RECEIVE);
        workTicket.mappingWorkDetail(savedWorkDetail);

        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        return createWorkDetailServiceResponse(savedWorkDetail);
    }

    @Transactional
    public WorkServiceResponse beginWorkDetailAnalysis(final String workTicketId, WorkTicketAnalyzeRequest request) {
        /* TODO 접수자 검증 로직 */
        Optional<WorkTicket> oWorkTicket = workTicketRepository.fetchWithWorkDetail(workTicketId);

        if (oWorkTicket.isEmpty()) {
            log.error("TicketId:{} is not present", workTicketId);
            throw new WorkClientException("TicketId:" + workTicketId + " is not present");
        }

        WorkTicket workTicket = oWorkTicket.get();
        /** 요청자 검증 **/
        if (!workTicket.isReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 분석 단계로 진입하려 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }

        /** 분석 단계로 진입이 가능한 상태인지 검증 **/
        if (workTicket.isNotAnalyzable()) {
            log.error("TicketId:{} can't begin analysis, work-status must be receive \n now work-status is {}",
                    workTicketId, workTicket.getWorkStatus());

            throw new WorkClientException("TicketId" + workTicketId + "can't begin analysis, work-status must be receive \n" +
                    "now work-status is " + workTicket.getWorkStatus());
        }

        /**  JPA Dirty Checking **/
        workTicket.changeWorkStatus(WorkStatus.ANALYZE_BEGIN);

        WorkDetail workDetail = workTicket.getWorkDetail();

        // 티켓 히스토리 저장
        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        // 응답 생성
        WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
        WorkDetailServiceResponse workDetailServiceResponse = createWorkDetailServiceResponse(workDetail);
        return new WorkServiceResponse(workTicketServiceResponse, workDetailServiceResponse);
    }

    @Transactional
    public WorkServiceResponse completeWorkDetailAnalysis(final String workTicketId, WorkTicketAnalyzeRequest request) {
        Optional<WorkTicket> oWorkTicket = workTicketRepository.fetchWithWorkDetail(workTicketId);

        if (oWorkTicket.isEmpty()) {
            log.error("TicketId:{} is not present", workTicketId);
            throw new WorkClientException("TicketId:" + workTicketId + " is not present");
        }

        WorkTicket workTicket = oWorkTicket.get();
        /** 요청자 검증 **/
        if (!workTicket.isReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 분석 단계를 완료하려 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }

        if (!WorkStatus.ANALYZE_BEGIN.equals(workTicket.getWorkStatus())) {
            log.error("TicketId:{} 작업 분석 단계가 아닌 상태에서 작업 분석을 완료하려고 합니다.", workTicketId);
            throw new WorkClientException("작업 분석 단계가 아닙니다.");
        }

        WorkDetail workDetail = workTicket.getWorkDetail();
        // dirty-checking
        workDetail.completeAnalyzeContent(request.analyzeContent());
        workTicket.changeWorkStatus(WorkStatus.ANALYZE_COMPLETE);

        WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
        WorkDetailServiceResponse workDetailServiceResponse = createWorkDetailServiceResponse(workDetail);
        return new WorkServiceResponse(workTicketServiceResponse, workDetailServiceResponse);
    }

    /** 계획 수립 단계 시작
     * workStatus -> MAKE_PLAN_BEGIN **/
    @Transactional
    public WorkServiceResponse beginWorkDetailPlan(final String workTicketId, WorkTicketPlanRequest request) {
        Optional<WorkTicket> oWorkTicket = workTicketRepository.fetchWithWorkDetail(workTicketId);

        if (oWorkTicket.isEmpty()) {
            log.error("TicketId:{} is not present", workTicketId);
            throw new WorkClientException("TicketId:" + workTicketId + " is not present");
        }

        WorkTicket workTicket = oWorkTicket.get();
        /** 요청자 검증 **/
        if (!workTicket.isReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 분석 단계를 완료하려 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }

        if (!WorkStatus.ANALYZE_COMPLETE.equals(workTicket.getWorkStatus())) {
            log.error("TicketId:{} 작업 분석 단계가 아닌 상태에서 작업 분석을 완료하려고 합니다.", workTicketId);
            throw new WorkClientException("작업 분석 단계가 아닙니다.");
        }
        //dirty-checking
        workTicket.changeWorkStatus(WorkStatus.MAKE_PLAN_BEGIN);
        WorkDetail workDetail = workTicket.getWorkDetail();

        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
        WorkDetailServiceResponse workDetailServiceResponse = createWorkDetailServiceResponse(workDetail);
        return new WorkServiceResponse(workTicketServiceResponse, workDetailServiceResponse);
    }

    /** 계획 수립 단계 종료 **/
    @Transactional
    public WorkServiceResponse completeWorkDetailPlan(String workTicketId, WorkTicketPlanRequest request) {
        Optional<WorkTicket> oWorkTicket = workTicketRepository.fetchWithWorkDetail(workTicketId);

        if (oWorkTicket.isEmpty()) {
            log.info("TicketId:{} is not present", workTicketId);
            throw new WorkClientException("TicketId:" + workTicketId + " is not present");
        }

        WorkTicket workTicket = oWorkTicket.get();
        /** 요청자 검증 **/
        if (!workTicket.isReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.info("TicketId:{} 접수자가 아닌 사용자가 계획 단계를 완료하려 합니다.", workTicketId);
            throw new WorkClientException(request.receiverId(), "잘못된 접근입니다.");
        }

        if (!WorkStatus.MAKE_PLAN_BEGIN.equals(workTicket.getWorkStatus())) {
            log.error("TicketId:{} 작업 계획 시작 단계가 아닌 상태에서 작업 계획 단계를 완료하려고 합니다.", workTicketId);
            throw new WorkClientException("작업 게획 시작 단계가 아닙니다.");
        }

        //dirty-checking
        workTicket.changeWorkStatus(WorkStatus.MAKE_PLAN_COMPLETE);
        WorkDetail workDetail = workTicket.getWorkDetail();
        workDetail.completeWorkPlan(request.workPlanContent());

        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
        WorkDetailServiceResponse workDetailServiceResponse = createWorkDetailServiceResponse(workDetail);
        return new WorkServiceResponse(workTicketServiceResponse, workDetailServiceResponse);
    }

    /** 결재 요청
     * workStatus -> REQUEST_CONFIRM **/
    @Transactional
    public void requestConfirm(String workTicketId, WorkTicketPlanRequest request) {
        Optional<WorkTicket> oWorkTicket = workTicketRepository.fetchWithWorkDetail(workTicketId);

        if (oWorkTicket.isEmpty()) {
            log.error("TicketId:{} is not present", workTicketId);
            throw new WorkClientException("TicketId:" + workTicketId + " is not present");
        }

        WorkTicket workTicket = oWorkTicket.get();
        /** 요청자 검증 **/
        if (!workTicket.isReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 결재를 요청하려 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }

        if (!WorkStatus.MAKE_PLAN_COMPLETE.equals(workTicket.getWorkStatus())) {
            log.error("TicketId:{} 작업 계획 시작 완료가 아닌 상태에서 결재를 요청하려 합니다.", workTicketId);
            throw new WorkClientException("작업 게획 완료 단계가 아닙니다.");
        }

        workTicket.changeWorkStatus(WorkStatus.REQUEST_CONFIRM);
        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        // 이벤트 -> 결재 문서 생성되도록...
        WorkRequester workRequester = workTicket.getWorkRequester();
        MemberLeave memberLeave = memberLeaveRepository.findMemberWithOrganizationFetch(workRequester.getId()).get();

        WorkDetail workDetail = workTicket.getWorkDetail();
        Map<String, Object> contents = new HashMap<>();
        contents.put("title", "업무 요청서");
        contents.put("requesterId", workTicket.getWorkRequester().getId());
        contents.put("requesterName", workTicket.getWorkRequester().getName());
        contents.put("requestDepartmentName", memberLeave.receiveDepartmentName());
        contents.put("requestTitle", workTicket.getRequestTitle());
        contents.put("requestContent", workTicket.getRequestContent());

        contents.put("chargeDepartmentId", workTicket.getChargeDepartmentId());
        contents.put("receiverId", workDetail.getReceiverId());
        contents.put("receiverName", workDetail.getReceiverName());
        contents.put("analyzeContent", workDetail.getAnalyzeContent());
        contents.put("workPlanContent", workDetail.getWorkPlanContent());

        ConfirmCreateForm confirmCreateForm = new ConfirmCreateForm(
                workTicket.getWorkTicketPk(),
                memberLeave.receiveCompanyId(),
                memberLeave.receiveDepartmentId(),
                memberLeave.receiveDepartmentName(),
                "GW",
                "WRK",
                workRequester.getId(),
                workRequester.getName(),
                contents);

        eventPublisher.publishEvent(new RestApiRequestEvent(
                confirmCreateForm,
                "POST",
                "http://localhost:8000",
                "/api/confirm-documents")
        );
    }

    @Transactional
    public void processingAfterConfirmComplete(Long workTicketPk, WorkTicketCompleteConfirmRequest request) {
        Optional<WorkTicket> oWorkTicket = workTicketRepository.fetchWithWorkDetail(workTicketPk);

        if (oWorkTicket.isEmpty()) {
            log.error("TicketPk:{} is not present", workTicketPk);
            throw new WorkClientException("TicketPk:" + workTicketPk + " is not present");
        }

        WorkTicket workTicket = oWorkTicket.get();
        String workTicketId = workTicket.getWorkTicketId();
        if (!WorkStatus.REQUEST_CONFIRM.equals(workTicket.getWorkStatus())) {
            log.error("TicketId:{} 결재 요청 단계가 아닌 상태에서 결재 요청 완료 후속 처리를 시도하려고 합니다.", workTicketId);
            throw new WorkClientException("결재 요청 단계가 아닙니다.");
        }
        // 처리해줘야함
        workTicket.changeWorkStatus(WorkStatus.valueOf(request.workStatus()));
        workTicketHistRepository.save(new WorkTicketHistory(workTicket));
    }

    /** 작업 단계 시작  workStatus 가 ACCEPT 일때만 진입 가능
     * workStatus -> WORKING **/
    @Transactional
    public void beginWorkDetailWorking() {

    }

    /** 작업 단계 종료
     * workStatus -> DONNE **/
    @Transactional
    public void completeWorkDetailWorking() {

    }


    private static WorkTicketServiceResponse createWorkTicketServiceResponse(WorkTicket savedWorkTicket) {
        return new WorkTicketServiceResponse(
                savedWorkTicket.getWorkTicketPk(),
                savedWorkTicket.getWorkTicketId(),
                savedWorkTicket.getWorkStatus().toString(),
                savedWorkTicket.getCreatedTime(),
                savedWorkTicket.getChargeCompanyId(),
                savedWorkTicket.getChargeDepartmentId(),
                savedWorkTicket.getModifiedTime(),
                savedWorkTicket.getRequestTitle(),
                savedWorkTicket.getRequestContent(),
                savedWorkTicket.getWorkRequester()
        );
    }

    private static WorkDetailServiceResponse createWorkDetailServiceResponse(WorkDetail savedWorkDetail) {
        return new WorkDetailServiceResponse(
                savedWorkDetail.getWorkDetailPk(),
                savedWorkDetail.getAnalyzeContent(),
                savedWorkDetail.getAnalyzeCompletedTime(),
                savedWorkDetail.getWorkPlanContent(),
                savedWorkDetail.getWorkPlanCompletedTime(),
                savedWorkDetail.getExpectDeadlineDate(),
                savedWorkDetail.getReceiverId(),
                savedWorkDetail.getReceiverName(),
                savedWorkDetail.getCreateTime(),
                savedWorkDetail.getPreReflect(),
                savedWorkDetail.getPreReflectReason());
    }


    private static WorkTicketHistory createWorkTicketHistory(WorkTicketCreateRequest request, WorkTicket savedWorkTicket) {
        return WorkTicketHistory.builder()
                .workTicketPk(savedWorkTicket.getWorkTicketPk())
                .workTicketId(savedWorkTicket.getWorkTicketId())
                .workStatus(savedWorkTicket.getWorkStatus())
                .createdTime(savedWorkTicket.getCreatedTime())
                .chargeCompanyId(request.chargeCompanyId())
                .chargeDepartmentId(savedWorkTicket.getChargeDepartmentId())
                .modifiedTime(savedWorkTicket.getModifiedTime())
                .requestTitle(savedWorkTicket.getRequestTitle())
                .requestContent(savedWorkTicket.getRequestContent())
                .workRequester(savedWorkTicket.getWorkRequester())
                .build();
    }
}
