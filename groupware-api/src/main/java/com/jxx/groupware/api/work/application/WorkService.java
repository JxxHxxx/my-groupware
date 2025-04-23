package com.jxx.groupware.api.work.application;

import com.jxx.groupware.api.file.domain.StorageService;
import com.jxx.groupware.api.file.domain.UploadFile;
import com.jxx.groupware.api.work.dto.response.*;
import com.jxx.groupware.core.ConfirmCreateForm;
import com.jxx.groupware.api.work.dto.request.*;
import com.jxx.groupware.api.work.listener.CreateConfirmThroughRestApiEvent;
import com.jxx.groupware.api.work.query.WorkTicketMapper;
import com.jxx.groupware.core.common.pagination.PageService;
import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.domain.entity.Organization;
import com.jxx.groupware.core.vacation.infra.MemberLeaveRepository;
import com.jxx.groupware.core.vacation.infra.OrganizationRepository;
import com.jxx.groupware.core.work.WorkResponseCode;
import com.jxx.groupware.core.work.domain.*;
import com.jxx.groupware.core.work.domain.exception.WorkClientException;
import com.jxx.groupware.core.work.dto.TicketReceiver;
import com.jxx.groupware.core.work.infra.WorkDetailRepository;
import com.jxx.groupware.core.work.infra.WorkTicketAttachmentRepository;
import com.jxx.groupware.core.work.infra.WorkTicketHistRepository;
import com.jxx.groupware.core.work.infra.WorkTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.jxx.groupware.core.work.WorkResponseCode.*;
import static com.jxx.groupware.core.work.domain.WorkStatus.*;

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
    private final OrganizationRepository organizationRepository; // 임시

    @Value("${third-part.confirm.host}")
    private String confirmServerHost;
    @Value("${third-part.confirm.api.create.url}")
    private String confirmCreateUrl;
    @Value("${third-part.confirm.api.create.method}")
    private String confirmCreateMethod;

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
        // UUID를 통해 동시 요청을 백엔드에서 차단하기 위한 파라미터
        String requestUUID = request.requestUUID();

        WorkTicket workTicket;
        WorkTicket.WorkTicketBuilder workTicketBuilder = WorkTicket.builder()
                .requestUUID(requestUUID)
                .workStatus(CREATE)
                .createdTime(LocalDateTime.now())
                .chargeCompanyId(request.chargeCompanyId())
                .chargeDepartmentId(request.chargeDepartmentId())
                .modifiedTime(LocalDateTime.now())
                .requestTitle(request.requestTitle())
                .requestContent(request.requestContent())
                .workRequester(request.workRequester());

        if (StringUtils.hasText(requestUUID)) {
            try {
                // UUID 인지 검증
                UUID.fromString(requestUUID);
                workTicket = workTicketBuilder
                        .requestUUID(requestUUID)
                        .build();

                // 이미 해당 workTicketId 가 존재할 경우 -> 즉 중복 요청일 경우
                if (workTicketRepository.findByWorkTicketId(requestUUID).isPresent()) {
                    throw new WorkClientException(WORK_F_002);
                };

            } catch (IllegalArgumentException e) {
                log.error("it is incorrect UUID format requestUUID:{}", requestUUID);
                throw new WorkClientException(WORK_F_003);
            }
        } else {
            workTicket = workTicketBuilder
                    .requestUUID(UUID.randomUUID().toString())
                    .build();
        }

        WorkTicket savedWorkTicket = workTicketRepository.save(workTicket);
        return createWorkTicketServiceResponse(savedWorkTicket);
    }

    // 파일 저장 후, 실행 해야함
    @Transactional
    public void saveAttachment(String workTicketId, UploadFile uploadFile) {
        WorkTicket workTicket = workTicketRepository.findByWorkTicketId(workTicketId)
                .orElseThrow(() -> {
                    log.error("workTicketId : {} is not present", workTicketId);
                    return new WorkClientException(WORK_F_004);
                });

        WorkTicketAttachment workTicketAttachment = WorkTicketAttachment.builder()
                .attachmentUrl(uploadFile.getStoreFilename())
                .uploadFilename(uploadFile.getUploadFilename())
                .workTicket(workTicket)
                .build();

        workTicketAttachmentRepository.save(workTicketAttachment);
    }

    public WorkTicketAttachmentResponse getWorkTicketAttachment(Long attachmentId) {
        WorkTicketAttachment attachment = workTicketAttachmentRepository.findById(attachmentId).get();
        return new WorkTicketAttachmentResponse(attachment.getUploadFilename(), attachment.getAttachmentUrl());
    }

    /**
     * 작업 티켓 PK 조회
     **/
    public WorkServiceResponse getWorkTicketByPk(Long workTicketPk) {
        WorkTicket workTicket = workTicketRepository.findById(workTicketPk)
                .orElseThrow(() -> new WorkClientException(workTicketPk + "에 해당하는 WorkTicket은 존재 않습니다."));

        /** 작업 티켓이 CREATE 상태일 때는 WorkDetail 이 존재하지 않기 때문에 분기 처리**/
        if (Objects.equals(workTicket.getWorkStatus(), WorkStatus.CREATE)) {
            WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
            return new WorkServiceResponse(workTicketServiceResponse, null);
        } else {
            WorkDetail workDetail = workTicket.getWorkDetail();
            return new WorkServiceResponse(createWorkTicketServiceResponse(workTicket), createWorkDetailServiceResponse(workDetail));
        }
    }

    /**
     * 작업 티켓 삭제
     * workStatus -> DELETE
     * 삭제 가능한 티켓의 workStatus : CREATE
     **/
    @Transactional
    public WorkTicketServiceResponse deleteWorkTicket(String workTicketId, WorkTicketDeleteRequest request) {
        WorkTicket workTicket = workTicketRepository.findByWorkTicketId(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });

        if (workTicket.isNotWorkStatus(CREATE)) {
            log.error("");
            throw new WorkClientException("이미 접수 단계에 진입한 작업 티켓은 삭제할 수 없습니다.");
        }
        ;

        // 삭제 가능한 사람인지 검증
        if (workTicket.isNotRequester(request.workRequester())) {
            log.error("티켓 요청자가 아닌 사용자가 작업 티켓을 삭제하려고 합니다. \n" +
                    "ticket requester:{}, delete requester:{}", workTicket.getWorkRequester().getId(), request.workRequester().getId());
            throw new WorkClientException("작업 티켓은 요청자만 삭제할 수 있습니다.");
        }
        ;

        // WRITE QUERY : JPA dirty checking
        workTicket.changeWorkStatusTo(DELETE);
        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        return createWorkTicketServiceResponse(workTicket);
    }

    /**
     * 접수자의 작업 반려
     * workStatus -> REJECT_FROM_CHARGE
     **/
    @Transactional
    public WorkServiceResponse rejectWorkTicketFromReceiver(String workTicketId, WorkTicketRejectRequest request) {
        WorkTicket workTicket = workTicketRepository.findByWorkTicketId(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });

        WorkManager workManager = new WorkManager(workTicket);
        workManager.rejectFromReceiver(request.rejectReason(), request.ticketReceiver());

        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        return new WorkServiceResponse(createWorkTicketServiceResponse(workTicket), createWorkDetailServiceResponse(workTicket.getWorkDetail()));
    }

    /**
     * 작업 티켓 검색
     **/
    public PageImpl<WorkTicketSearchResponse> searchWorkTicket(WorkTickSearchCond searchCond, int page, int size) {
        PageService pageService = new PageService(page, size);
        List<WorkTicketSearchResponse> response = workTicketMapper.search(searchCond);
        return pageService.convertToPage(response);
    }

    /**
     * 작업 티켓 접수 : 접수에서 WorkDetail 만들어짐
     **/
    @Transactional
    public WorkDetailServiceResponse receiveWorkTicketAndCreateWorkDetail(final String workTicketId, WorkTicketReceiveRequest request) {
        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });

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

        //WRITE QUERY : JPA Dirty Checking
        workTicket.changeWorkStatusTo(WorkStatus.RECEIVE);
        workTicket.mappingWorkDetail(savedWorkDetail);

        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        return createWorkDetailServiceResponse(savedWorkDetail);
    }

    @Transactional
    public WorkServiceResponse beginWorkDetailAnalysis(final String workTicketId, WorkTicketAnalyzeRequest request) {
        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });

        /** 요청자 검증 **/
        if (!workTicket.isNotReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
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
        // WRITE QUERY : JPA Dirty Checking
        workTicket.changeWorkStatusTo(ANALYZE_BEGIN);
        WorkDetail workDetail = workTicket.getWorkDetail();
        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        return new WorkServiceResponse(createWorkTicketServiceResponse(workTicket), createWorkDetailServiceResponse(workDetail));
    }

    @Transactional
    public WorkServiceResponse completeWorkDetailAnalysis(final String workTicketId, WorkTicketAnalyzeRequest request) {
        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });

        /** 요청자 검증 **/
        if (!workTicket.isNotReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 분석 단계를 완료하려 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }

        if (workTicket.isNotWorkStatus(ANALYZE_BEGIN)) {
            log.error("TicketId:{} 작업 분석 단계가 아닌 상태에서 작업 분석을 완료하려고 합니다.", workTicketId);
            throw new WorkClientException("작업 분석 단계가 아닙니다.");
        }

        WorkDetail workDetail = workTicket.getWorkDetail();
        // WRITE QUERY : JPA dirty-checking
        workDetail.completeAnalyzeContent(request.analyzeContent());
        workTicket.changeWorkStatusTo(ANALYZE_COMPLETE);

        WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
        WorkDetailServiceResponse workDetailServiceResponse = createWorkDetailServiceResponse(workDetail);
        return new WorkServiceResponse(workTicketServiceResponse, workDetailServiceResponse);
    }

    /**
     * 계획 수립 단계 시작
     * workStatus -> MAKE_PLAN_BEGIN
     **/
    @Transactional
    public WorkServiceResponse beginWorkDetailPlan(final String workTicketId, WorkTicketPlanRequest request) {
        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });


        /** 요청자 검증 **/
        if (!workTicket.isNotReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 분석 단계를 완료하려 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }

        if (workTicket.isNotWorkStatus(ANALYZE_COMPLETE)) {
            log.error("TicketId:{} 작업 분석 단계가 아닌 상태에서 작업 분석을 완료하려고 합니다.", workTicketId);
            throw new WorkClientException("작업 분석 단계가 아닙니다.");
        }
        // WRITE QUERY : JPA dirty-checking
        workTicket.changeWorkStatusTo(MAKE_PLAN_BEGIN);
        WorkDetail workDetail = workTicket.getWorkDetail();

        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
        WorkDetailServiceResponse workDetailServiceResponse = createWorkDetailServiceResponse(workDetail);
        return new WorkServiceResponse(workTicketServiceResponse, workDetailServiceResponse);
    }

    /**
     * 계획 수립 단계 종료
     **/
    @Transactional
    public WorkServiceResponse completeWorkDetailPlan(final String workTicketId, WorkTicketPlanRequest request) {
        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });

        /** 요청자 검증 **/
        if (!workTicket.isNotReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.info("TicketId:{} 접수자가 아닌 사용자{}가 계획 단계를 완료하려 합니다.", workTicketId, request.receiverId());
            throw new WorkClientException(WorkResponseCode.WORK_F_001);
        }

        if (workTicket.isNotWorkStatus(MAKE_PLAN_BEGIN)) {
            log.error("TicketId:{} 작업 계획 시작 단계가 아닌 상태에서 작업 계획 단계를 완료하려고 합니다.", workTicketId);
            throw new WorkClientException("작업 게획 시작 단계가 아닙니다.");
        }

        // WRITE QUERY : dirty-checking
        workTicket.changeWorkStatusTo(WorkStatus.MAKE_PLAN_COMPLETE);
        WorkDetail workDetail = workTicket.getWorkDetail();
        workDetail.completeWorkPlan(request.workPlanContent());

        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        WorkTicketServiceResponse workTicketServiceResponse = createWorkTicketServiceResponse(workTicket);
        WorkDetailServiceResponse workDetailServiceResponse = createWorkDetailServiceResponse(workDetail);
        return new WorkServiceResponse(workTicketServiceResponse, workDetailServiceResponse);
    }

    /**
     * 결재 요청
     * workStatus -> REQUEST_CONFIRM
     **/
    @Transactional
    public void requestConfirmForWorkTicket(final String workTicketId, WorkTicketPlanRequest request) {
        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });

        /** 요청자 검증 **/
        if (!workTicket.isNotReceiverRequest(request.receiverId(), request.receiverCompanyId(), request.receiverDepartmentId())) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 결재를 요청하려 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }

        if (workTicket.isNotWorkStatus(MAKE_PLAN_COMPLETE)) {
            log.error("TicketId:{} 작업 계획 시작 완료가 아닌 상태에서 결재를 요청하려 합니다.", workTicketId);
            throw new WorkClientException("작업 게획 완료 단계가 아닙니다.");
        }

        workTicket.changeWorkStatusTo(WorkStatus.REQUEST_CONFIRM);
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

        // ASYNC
        eventPublisher.publishEvent(new CreateConfirmThroughRestApiEvent(confirmCreateForm,
                confirmCreateMethod, confirmServerHost, confirmCreateUrl)
        );
    }

    @Transactional
    public void processingAfterConfirmComplete(final Long workTicketPk, WorkTicketCompleteConfirmRequest request) {
        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketPk)
                .orElseThrow(() -> {
                    log.error("TicketPk:{} is not present", workTicketPk);
                    throw new WorkClientException("TicketPk:" + workTicketPk + " is not present");
                });

        String workTicketId = workTicket.getWorkTicketId();
        if (workTicket.isNotWorkStatus(WorkStatus.REQUEST_CONFIRM)) {
            log.error("TicketId:{} 결재 요청 단계가 아닌 상태에서 결재 요청 완료 후속 처리를 시도하려고 합니다.", workTicketId);
            throw new WorkClientException("결재 요청 단계가 아닙니다.");
        }
        // 처리해줘야함
        workTicket.changeWorkStatusTo(WorkStatus.valueOf(request.workStatus()));
        workTicketHistRepository.save(new WorkTicketHistory(workTicket));
    }

    /**
     * 작업 단계 시작  workStatus 가 ACCEPT 일때만 진입 가능
     * workStatus
     * AS-IS - ACCEPT OR REQUEST_CONFIRM + PRE_REFLECT = true
     * TO-BE - WORKING
     **/
    @Transactional
    public WorkServiceResponse beginWork(final String workTicketId, WorkTicketBeginWorkRequest request) {
        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketId:{} is not present", workTicketId);
                    throw new WorkClientException("TicketId:" + workTicketId + " is not present");
                });

        // TODO REQUEST_CONFIRM + PRE_REFLECT = true 인 경우 넘어갈 수 있도록 해야함

        TicketReceiver ticketReceiver = request.ticketReceiver();
        if (workTicket.isNotReceiverRequest(ticketReceiver)) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 작업을 시작하려고 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }
        ;

        if (workTicket.isNotWorkStatus(WorkStatus.ACCEPT)) {
            log.error("TicketId:{} 결재 요청 승인이 아닌 상태에서 작업을 시작하려고 합니다.", workTicketId);
            throw new WorkClientException("졀재 요청 승인 상태가 아닙니다.");
        }
        // WRITE QUERY : JPA dirty-checking 작업 시작 단계로 변경
        workTicket.changeWorkStatusTo(WorkStatus.WORKING);
        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        return new WorkServiceResponse(createWorkTicketServiceResponse(workTicket), createWorkDetailServiceResponse(workTicket.getWorkDetail()));
    }

    /**
     * 작업 단계 종료
     * workStatus
     * AS-IS - WORKING
     * TO-BE - DONE
     **/
    @Transactional
    public WorkServiceResponse completeWork(final String workTicketId, WorkTicketCompleteRequest request) {

        // TODO 선처리인 경우, 결재가 완료됐는지 확인이 필요함, WorkTicket 새로운 컬럼 추가 필요.

        WorkTicket workTicket = workTicketRepository.fetchWithWorkDetail(workTicketId)
                .orElseThrow(() -> {
                    log.error("TicketPk:{} is not present", workTicketId);
                    throw new WorkClientException("TicketPk:" + workTicketId + " is not present");
                });

        TicketReceiver ticketReceiver = request.ticketReceiver();
        if (workTicket.isNotReceiverRequest(ticketReceiver)) {
            log.error("TicketId:{} 접수자가 아닌 사용자가 작업을 완료하려고 합니다.", workTicketId);
            throw new WorkClientException("잘못된 접근입니다.");
        }
        ;

        if (workTicket.isNotWorkStatus(WorkStatus.WORKING)) {
            log.error("TicketId:{} 작업 시작 단계가 아닌 상태에서 작업을 완료하려고 합니다.", workTicketId);
            throw new WorkClientException("작업 티켓이 시작 단계 상태가 아닙니다.");
        }

        // WRITE QUERY : JPA dirty-checking 작업 종료 단계로 변경
        workTicket.changeWorkStatusTo(WorkStatus.DONE);
        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        return new WorkServiceResponse(createWorkTicketServiceResponse(workTicket), createWorkDetailServiceResponse(workTicket.getWorkDetail()));
    }

    /**
     * 작업 선처리 -> REQUEST_CONFIRM(결재 요청) 단계에서만 가능
     * 결재가 완료됐을 경우, 이미 결재가 완료됐다고 안내
     * 다른 단계일 경우, 잘못된 접근으로 처리
     **/
    public WorkServiceResponse preReflect(String workTicketId, Object request) {
        return null;
    }

    private WorkTicketServiceResponse createWorkTicketServiceResponse(WorkTicket savedWorkTicket) {
        // TODO 임시 로직 추후에는 JOIN으로 한 번에 가져오는게 바람직하다 봄
        Organization organization = organizationRepository.findOrganizationByCompanyIdAndDepartmentId(
                savedWorkTicket.getChargeCompanyId(), savedWorkTicket.getChargeDepartmentId()).get();

        return new WorkTicketServiceResponse(
                savedWorkTicket.getWorkTicketPk(),
                savedWorkTicket.getWorkTicketId(),
                savedWorkTicket.getWorkStatus().toString(),
                savedWorkTicket.getCreatedTime(),
                savedWorkTicket.getChargeCompanyId(),
                savedWorkTicket.getChargeDepartmentId(),
                organization.getDepartmentName(),
                savedWorkTicket.getModifiedTime(),
                savedWorkTicket.getRequestTitle(),
                savedWorkTicket.getRequestContent(),
                savedWorkTicket.getWorkRequester()
        );
    }

    private WorkDetailServiceResponse createWorkDetailServiceResponse(WorkDetail savedWorkDetail) {
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
}
