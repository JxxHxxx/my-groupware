package com.jxx.vacation.core.message.body.vendor.confirm;

import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    public VacationConfirmModel(String confirmStatus, String createSystem, LocalDateTime createTime, String confirmDocumentId,
                                String documentType, String companyId, String departmentId, String requesterId, String approvalLineLifeCycle, Long contentPk) {
        this.confirmStatus = confirmStatus;
        this.createSystem = createSystem;
        this.createTime = createTime;
        this.confirmDocumentId = confirmDocumentId;
        this.documentType = documentType;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.requesterId = requesterId;
        this.approvalLineLifeCycle = approvalLineLifeCycle;
        this.contentPk = contentPk;
    }

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
        String confirmStatus;
        if (messageBody.get("confirm_status") instanceof ConfirmStatus receivedConfirmStatus) {
            confirmStatus = String.valueOf(receivedConfirmStatus);
        } else {
            confirmStatus = (String) messageBody.get("confirm_status");
        }

        String confirmDocumentId = String.valueOf(messageBody.get("confirm_document_id"));
        String createSystem = (String) messageBody.get("create_system");
        String documentType = String.valueOf(messageBody.get("document_type"));
        String companyId = (String) messageBody.get("company_id");
        String departmentId = (String) messageBody.get("department_id");
        String requesterId = (String) messageBody.get("requester_id");
        String approvalLineLifeCycle =  String.valueOf(messageBody.get("approval_line_life_cycle"));
        LocalDateTime createTime = convertToCreateTime(messageBody);

        return new VacationConfirmModel(confirmStatus, confirmDocumentId, createSystem, createTime, documentType, companyId, departmentId, requesterId, approvalLineLifeCycle);
    }

    private static LocalDateTime convertToCreateTime(Map<String, Object> body) {
        List<Integer> createTimes = null;
        Integer year = null;
        Integer month = null;
        Integer dayOfMonth = null;
        Integer hour = null;
        Integer minute = null;
        Integer second = null;
        Integer nanoOfSecond = null;

        if (body.get("create_time") instanceof LocalDateTime createTime) {
            return createTime;
        }

        try {
            createTimes = (ArrayList) body.get("create_time");
            year = createTimes.get(0);
            month = createTimes.get(1);
            dayOfMonth = createTimes.get(2);
            hour = createTimes.get(3);
            minute = createTimes.get(4);
            second = createTimes.get(5);
            nanoOfSecond = createTimes.get(6);
        } catch (ClassCastException e) {
            Integer[] arrayCreateTimes = (Integer[]) body.get("create_time");
            if (arrayCreateTimes.length == 7) {
                year = arrayCreateTimes[0];
                month = arrayCreateTimes[1];
                dayOfMonth = arrayCreateTimes[2];
                hour = arrayCreateTimes[3];
                minute = arrayCreateTimes[4];
                second = arrayCreateTimes[5];
                nanoOfSecond = arrayCreateTimes[6];
            } else  {
                throw new VacationClientException("메시지 필드 중 createTime 처리를 할 수 없습니다. createTime: " + Arrays.toString(arrayCreateTimes));
            }
        }

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
    }
}
