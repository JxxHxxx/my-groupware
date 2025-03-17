package com.jxx.groupware.core.messaging.body.vendor.confirm;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static com.jxx.groupware.core.messaging.MessageConst.CREATE_SYSTEM;
import static com.jxx.groupware.core.messaging.body.vendor.confirm.ConfirmStatus.CREATE;

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

    private final String requesterName;
    private final String departmentName;
    private final List<VacationDurationModel> vacationDurations;

    public CommonVacationConfirmMessageForm(ConfirmStatus confirmStatus, String requesterId, String companyId, String departmentId,
                                            String createSystem, DocumentType documentType, float vacationDate, Long vacationId,
                                            LocalDateTime createTime, ApprovalLineLifecycle approvalLineLifeCycle, String requesterName,
                                            String departmentName, List<VacationDurationModel> vacationDurations) {
        this.confirmStatus = confirmStatus;
        this.requesterId = requesterId;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.createSystem = createSystem;
        this.documentType = documentType;
        this.vacationDate = vacationDate;
        this.vacationId = vacationId;
        this.createTime = createTime;
        this.approvalLineLifeCycle = approvalLineLifeCycle;
        this.requesterName = requesterName;
        this.departmentName = departmentName;
        this.vacationDurations = vacationDurations;
    }

    public static CommonVacationConfirmMessageForm create(String requesterId, String companyId, String departmentId,
                                                          float vacationDate, Long vacationId, String requesterName,
                                                          String departmentName, List<VacationDurationModel> vacationDurations) {
        return new CommonVacationConfirmMessageForm(
                CREATE,
                requesterId,
                companyId,
                departmentId,
                CREATE_SYSTEM,
                DocumentType.VAC,
                vacationDate,
                vacationId,
                LocalDateTime.now(),
                ApprovalLineLifecycle.BEFORE_CREATE,
                requesterName,
                departmentName,
                vacationDurations);
    }
}
