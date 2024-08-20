package com.jxx.groupware.api.work.application;

import com.jxx.groupware.api.work.dto.request.WorkTickSearchCond;
import com.jxx.groupware.api.work.dto.request.WorkTicketCreateRequest;
import com.jxx.groupware.api.work.dto.request.WorkTicketReceiveRequest;
import com.jxx.groupware.api.work.dto.response.WorkDetailServiceResponse;
import com.jxx.groupware.api.work.dto.response.WorkTicketServiceResponse;
import com.jxx.groupware.api.work.query.WorkTicketMapper;
import com.jxx.groupware.core.work.domain.WorkDetail;
import com.jxx.groupware.core.work.domain.WorkStatus;
import com.jxx.groupware.core.work.domain.WorkTicket;
import com.jxx.groupware.core.work.domain.WorkTicketHistory;
import com.jxx.groupware.core.work.domain.exception.WorkClientException;
import com.jxx.groupware.core.work.infra.WorkDetailRepository;
import com.jxx.groupware.core.work.infra.WorkTicketAttachmentRepository;
import com.jxx.groupware.core.work.infra.WorkTicketHistRepository;
import com.jxx.groupware.core.work.infra.WorkTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkTicketRepository workTicketRepository;
    private final WorkTicketHistRepository workTicketHistRepository;
    private final WorkTicketMapper workTicketMapper;
    private final WorkDetailRepository workDetailRepository;
    private final WorkTicketAttachmentRepository workTicketAttachmentRepository;

    @Transactional
    public WorkTicketServiceResponse createWorkTicket(WorkTicketCreateRequest workTicketCreateRequest) {
        // TODO 회사, 부서 코드, 사용자 ID 유효성 검증을 고민해야 함

        WorkTicket workTicket = WorkTicket.builder()
                .workStatus(WorkStatus.CREATE)
                .createdTime(LocalDateTime.now())
                .chargeCompanyId(workTicketCreateRequest.chargeCompanyId())
                .chargeDepartmentId(workTicketCreateRequest.chargeDepartmentId())
                .modifiedTime(LocalDateTime.now())
                .requestTitle(workTicketCreateRequest.requestTitle())
                .requestContent(workTicketCreateRequest.requestContent())
                .workRequester(workTicketCreateRequest.workRequester())
                .build();

        WorkTicket savedWorkTicket = workTicketRepository.save(workTicket);
        log.info("success save workTicket {}", savedWorkTicket.getWorkTicketId());

        WorkTicketHistory workTicketHistory = WorkTicketHistory.builder()
                .workTicketPk(savedWorkTicket.getWorkTicketPk())
                .workTicketId(savedWorkTicket.getWorkTicketId())
                .workStatus(savedWorkTicket.getWorkStatus())
                .createdTime(savedWorkTicket.getCreatedTime())
                .chargeCompanyId(workTicketCreateRequest.chargeCompanyId())
                .chargeDepartmentId(savedWorkTicket.getChargeDepartmentId())
                .modifiedTime(savedWorkTicket.getModifiedTime())
                .requestTitle(savedWorkTicket.getRequestTitle())
                .requestContent(savedWorkTicket.getRequestContent())
                .workRequester(savedWorkTicket.getWorkRequester())
                .build();

        WorkTicketHistory savedWorkTicketHistory = workTicketHistRepository.save(workTicketHistory);
        log.info("success save workTicket history {}", savedWorkTicketHistory.getWorkTicketId());

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

    public List<WorkTicketServiceResponse> searchWorkTicket(WorkTickSearchCond searchCond) {
        return workTicketMapper.search(searchCond);

    }

    @Transactional
    public WorkDetailServiceResponse receiveWorkTicket(String workTicketId, WorkTicketReceiveRequest request) {
        WorkTicket workTicket = workTicketRepository.findByWorkTicketId(workTicketId)
                .orElseThrow(() -> new WorkClientException("workTicket workTicketId:" + workTicketId + " 는 존재하지 않습니다."));

        WorkDetail workDetail = WorkDetail.builder()
                .receiverId(request.receiverId())
                .receiverName(request.receiverName())
                .createTime(LocalDateTime.now())
                .build();

        WorkDetail savedWorkDetail = workDetailRepository.save(workDetail);
        // 접수자에 대한 검증 로직 추가 이벤트로 처리할 에정

        // Dirty Checking
        workTicket.changeWorkStatus(WorkStatus.RECEIVE);
        workTicket.mappingWorkDetail(savedWorkDetail);

        workTicketHistRepository.save(new WorkTicketHistory(workTicket));

        return new WorkDetailServiceResponse(
                savedWorkDetail.getWorkDetailPk(),
                savedWorkDetail.getAnalyzeContent(),
                workDetail.getAnalyzeCompletedTime(),
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
