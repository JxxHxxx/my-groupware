package com.jxx.vacation.messaging.infra;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VacationConfirmForm {

    private String companyId;
    private String departmentId;
    private String createSystem;
    private String confirmStatus;
    private String documentType;
    private String approvalId;

    public VacationConfirmForm(String companyId, String departmentId, String confirmStatus) {
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.confirmStatus = confirmStatus;
        this.createSystem = "JXX-MESSAGING";
        this.documentType = "VACATION";
        this.approvalId = null;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }
}
