package com.jxx.vacation.core.message.body.vendor.confirm;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static com.jxx.vacation.core.message.MessageConst.CREATE_SYSTEM;
import static com.jxx.vacation.core.message.body.vendor.confirm.ConfirmStatus.*;

/**
 * 휴가 결재(신청, 취소)에 필요한 데이터
 */

@Getter
public class VacationConfirmMessageForm {

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

    private final String title;
    private final String delegatorName;
    private final String reason;
    private final String requesterName;
    private final String departmentName;
    private final List<VacationDurationModel> vacationDurations;
    
    private VacationConfirmMessageForm(ConfirmStatus confirmStatus, String requesterId, String companyId,
                                       String departmentId, String createSystem, DocumentType documentType,
                                       float vacationDate, Long vacationId, ApprovalLineLifecycle approvalLineLifeCycle,
                                       String title, String delegatorName, String reason, String requesterName, String departmentName,
                                       List<VacationDurationModel> vacationDurations) {
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
        this.title = title;
        this.delegatorName = delegatorName;
        this.reason = reason;
        this.requesterName = requesterName;
        this.departmentName = departmentName;
        this.vacationDurations = vacationDurations;
    }

    public static VacationConfirmMessageForm create(String requesterId, String companyId, String departmentId,
                                                    float vacationDate, Long vacationId, String title, String delegatorId,
                                                    String reason, String requesterName, String departmentName,
                                                    List<VacationDurationModel> vacationDurations) {
        return new VacationConfirmMessageForm(
                CREATE,
                requesterId,
                companyId,
                departmentId,
                CREATE_SYSTEM,
                DocumentType.VAC,
                vacationDate,
                vacationId,
                ApprovalLineLifecycle.BEFORE_CREATE,
                title,
                delegatorId,
                reason,
                requesterName,
                departmentName,
                vacationDurations);
    }
}
