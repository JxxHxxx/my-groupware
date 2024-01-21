package com.jxx.vacation.core.message.payload.approval.form;

import com.jxx.vacation.core.message.payload.approval.ConfirmStatus;
import com.jxx.vacation.core.message.payload.approval.DocumentType;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.jxx.vacation.core.message.payload.approval.ConfirmStatus.*;

/**
 * 휴가 결재(신청, 취소)에 필요한 데이터
 */

@Getter
public class VacationApprovalForm {
    private final ConfirmStatus confirmStatus;
    private final String requesterId;
    private final String companyId;
    private final String departmentId;
    private final String createSystem;
    private final DocumentType documentType;
    private final float vacationDate;
    private final Long vacationId;
    private final LocalDateTime createTime;

    private VacationApprovalForm(ConfirmStatus confirmStatus, String requesterId, String companyId, String departmentId,
                                 String createSystem, DocumentType documentType, float vacationDate, Long vacationId) {
        this.confirmStatus = confirmStatus;
        this.requesterId = requesterId;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.createSystem = createSystem;
        this.documentType = documentType;
        this.vacationDate = vacationDate;
        this.vacationId = vacationId;
        this.createTime = LocalDateTime.now();
    }

    public static VacationApprovalForm create(String requesterId, String companyId, String departmentId, String createSystem,
                                              DocumentType documentType, float vacationDate, Long vacationId) {
        return new VacationApprovalForm(CREATE, requesterId, companyId, departmentId, createSystem, documentType, vacationDate, vacationId);
    }
}
