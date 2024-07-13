package com.jxx.vacation.core.message.body.vendor.confirm;

import lombok.Getter;

import java.time.LocalDateTime;

import static com.jxx.vacation.core.message.MessageConst.CREATE_SYSTEM;
import static com.jxx.vacation.core.message.body.vendor.confirm.ConfirmStatus.CREATE;

/**
 * 휴가 결재(신청, 취소)에 필요한 데이터
 */

@Getter
public class CommonVacationConfirmMessageForm {

    private final ConfirmStatus confirmStatus;
    private final String requesterId;
    private final String companyId;
    private final String departmentId;
    private final String createSystem;
    private final DocumentType documentType;
    private final float vacationDate;
    private final Long vacationId;
    private final LocalDateTime createTime;
    private final ApprovalLineLifecycle approvalLineLifeCycle;

    private CommonVacationConfirmMessageForm(ConfirmStatus confirmStatus, String requesterId, String companyId,
                                             String departmentId, String createSystem, DocumentType documentType,
                                             float vacationDate, Long vacationId, ApprovalLineLifecycle approvalLineLifeCycle) {
        this.confirmStatus = confirmStatus;
        this.requesterId = requesterId;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.createSystem = createSystem;
        this.documentType = documentType;
        this.vacationDate = vacationDate;
        this.vacationId = vacationId;
        this.approvalLineLifeCycle = approvalLineLifeCycle;
        this.createTime = LocalDateTime.now();
    }

    public static CommonVacationConfirmMessageForm create(String requesterId, String companyId, String departmentId,
                                                          float vacationDate, Long vacationId) {
        return new CommonVacationConfirmMessageForm(
                CREATE,
                requesterId,
                companyId,
                departmentId,
                CREATE_SYSTEM,
                DocumentType.VAC,
                vacationDate,
                vacationId,
                ApprovalLineLifecycle.BEFORE_CREATE);
    }
}
