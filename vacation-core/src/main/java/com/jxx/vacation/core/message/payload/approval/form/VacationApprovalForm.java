package com.jxx.vacation.core.message.payload.approval.form;

import com.jxx.vacation.core.message.payload.approval.ApprovalStatus;
import lombok.Getter;

/**
 * 휴가 결재(신청, 취소)에 필요한 데이터
 */

@Getter
public class VacationApprovalForm {
    private final ApprovalStatus approvalStatus;
    private final String requesterId;
    private final String companyId;
    private final String departmentId;
    private final float vacationDate;

    private VacationApprovalForm(ApprovalStatus approvalStatus, String requesterId, String companyId, String departmentId, float vacationDate) {
        this.approvalStatus = approvalStatus;
        this.requesterId = requesterId;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.vacationDate = vacationDate;
    }

    public static VacationApprovalForm create(String requesterId, String companyId, String departmentId, float vacationDate) {
        return new VacationApprovalForm(ApprovalStatus.CREATE, requesterId, companyId, departmentId, vacationDate);
    }
}
