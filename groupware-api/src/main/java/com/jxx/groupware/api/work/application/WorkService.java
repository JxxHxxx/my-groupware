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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @Transactional
    public WorkTicketServiceResponse createWorkTicket(WorkTicketCreateRequest request) {
        // 요청자가 다른 회사에 요청을 하려는 경우 제외
        if (!Objects.equals(request.workRequester().getCompanyId(), request.chargeCompanyId())) {
            throw new WorkClientException("다른 회사에 요청을 할 수 없습니다.");
        }
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

        WorkTicketHistory workTicketHistory = WorkTicketHistory.builder()
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

        if (workTicket.isNotReceivable()) {
            log.error("\n workTicketId:{} is already received or can't receive \n " +
                    "now workStatus is {}", workTicket.getWorkTicketId(), workTicket.getWorkStatus());
            throw new WorkClientException("티켓:" + workTicketId + "이미 접수되었거나 접수할 수 없는 상태입니다.");
        }

        WorkDetail workDetail = WorkDetail.builder()
                .receiverId(request.receiverId())
                .receiverName(request.receiverName())
                .createTime(LocalDateTime.now())
                .build();

        WorkDetail savedWorkDetail = workDetailRepository.save(workDetail);
        // Event 처리 - 접수자가 요청 대상 부서의 소속인지 검증
        eventPublisher.publishEvent(
                new WorkTicketReceiveEvent(
                        request.receiverId(),
                        request.receiverCompanyId(),
                        request.receiverDepartmentId(),
                        workTicket.getChargeCompanyId(),
                        workTicket.getChargeDepartmentId()));

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
