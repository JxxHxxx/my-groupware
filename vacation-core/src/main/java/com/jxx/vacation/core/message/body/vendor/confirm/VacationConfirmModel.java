package com.jxx.vacation.core.message.body.vendor.confirm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * VacationConfirmForm : MessageQ body 를 만들기 위한 토대 객체
 * MessageBodyBuilder : MessageQ body 필드가 Map 타입임 convert object(VacationConfirmForm) to Map
 * VacationConfirmModel : convert map(body) to object, SQL로 변환되는 모델
 */
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
    private Long contentPk;

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

    public static VacationConfirmModel from(Map<String, Object> messageBody) {
        String confirmStatus = (String) messageBody.get("confirm_status");
        String confirmDocumentId = String.valueOf(messageBody.get("confirm_document_id"));
        String createSystem = (String) messageBody.get("create_system");
        String documentType = (String) messageBody.get("document_type");
        String companyId = (String) messageBody.get("company_id");
        String departmentId = (String) messageBody.get("department_id");
        String requesterId = (String) messageBody.get("requester_id");
        String approvalLineLifeCycle = (String) messageBody.get("approval_line_life_cycle");
        LocalDateTime createTime = convertToCreateTime(messageBody);

        return new VacationConfirmModel(confirmStatus, confirmDocumentId, createSystem, createTime, documentType, companyId, departmentId, requesterId, approvalLineLifeCycle);
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
