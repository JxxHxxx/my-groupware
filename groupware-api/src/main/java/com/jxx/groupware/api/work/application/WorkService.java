package com.jxx.groupware.api.work.application;

import com.jxx.groupware.api.work.dto.response.WorkTicketCreateResponse;
import com.jxx.groupware.core.work.domain.WorkStatus;
import com.jxx.groupware.core.work.domain.WorkTicket;
import com.jxx.groupware.core.work.domain.WorkTicketHistory;
import com.jxx.groupware.core.work.infra.WorkDetailRepository;
import com.jxx.groupware.core.work.infra.WorkTicketAttachmentRepository;
import com.jxx.groupware.core.work.infra.WorkTicketHistRepository;
import com.jxx.groupware.core.work.infra.WorkTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkTicketRepository workTicketRepository;
    private final WorkTicketHistRepository workTicketHistRepository;
    private final WorkDetailRepository workDetailRepository;
    private final WorkTicketAttachmentRepository workTicketAttachmentRepository;

    @Transactional
    public WorkTicketCreateResponse createWorkTicket(WorkTicketCreateRequest workTicketCreateRequest) {
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

        return new WorkTicketCreateResponse(
                savedWorkTicket.getWorkTicketPk(),
                savedWorkTicket.getWorkTicketId(),
                savedWorkTicket.getWorkStatus(),
                savedWorkTicket.getCreatedTime(),
                savedWorkTicket.getChargeCompanyId(),
                savedWorkTicket.getChargeDepartmentId(),
                savedWorkTicket.getModifiedTime(),
                savedWorkTicket.getRequestTitle(),
                savedWorkTicket.getRequestContent(),
                savedWorkTicket.getWorkRequester()
        );
    }
}
