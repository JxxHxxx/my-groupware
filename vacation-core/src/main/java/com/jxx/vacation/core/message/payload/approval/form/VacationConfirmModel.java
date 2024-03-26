package com.jxx.vacation.core.message.payload.approval.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private VacationConfirmModel(String confirmStatus, String confirmDocumentId, String createSystem, LocalDateTime createTime, String documentType, String companyId,
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

    public static VacationConfirmModel from(Map<String, Object> body) {
        String confirmStatus = (String) body.get("confirm_status");
        String confirmDocumentId = String.valueOf(body.get("confirm_document_id"));
        String createSystem = (String) body.get("create_system");
        String documentType = (String) body.get("document_type");
        String companyId = (String) body.get("company_id");
        String departmentId = (String) body.get("department_id");
        String requesterId = (String) body.get("requester_id");
        String approvalLineStatus = (String) body.get("approval_line_life_cycle");
        LocalDateTime createTime = convertToCreateTime(body);

        return new VacationConfirmModel(confirmStatus, confirmDocumentId, createSystem, createTime, documentType, companyId, departmentId, requesterId, approvalLineStatus);
    }

    private static LocalDateTime convertToCreateTime(Map<String, Object> body) {
        List<Integer> createTimes = (ArrayList) body.get("create_time");
        Integer year = createTimes.get(0);
        Integer month = createTimes.get(1);
        Integer dayOfMonth = createTimes.get(2);
        Integer hour = createTimes.get(3);
        Integer minute = createTimes.get(4);
        Integer second = createTimes.get(5);
        Integer nanoOfSecond = createTimes.get(6);

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
    }
}
