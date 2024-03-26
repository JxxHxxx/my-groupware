package com.jxx.vacation.messaging.infra;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

// approval 서버 데이터베이스에 넣을 쿼리 모델
@Getter
@Setter
@ToString
public class VacationConfirmModel {

    private String confirmStatus;
    private String createSystem;
    private LocalDateTime createTime;
    private String confirmDocumentId;
    private String documentType;
    private String companyId;
    private String departmentId;
    private String requesterId;
    private String approvalLineLifeCycle;

    public VacationConfirmModel(String confirmStatus, String confirmDocumentId, String createSystem, LocalDateTime createTime, String documentType, String companyId,
                                String departmentId, String requesterId, String approvalLineLifeCycle) {
        this.confirmStatus = confirmStatus;
        this.createSystem = createSystem;
        this.createTime = createTime;
        this.confirmDocumentId = confirmDocumentId;
        this.documentType = documentType;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.requesterId = requesterId;
        this.approvalLineLifeCycle = approvalLineLifeCycle;
    }
}
