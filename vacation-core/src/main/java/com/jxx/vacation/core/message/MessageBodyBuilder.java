package com.jxx.vacation.core.message;

import com.jxx.vacation.core.message.payload.approval.form.VacationApprovalForm;

import java.util.HashMap;
import java.util.Map;


public class MessageBodyBuilder {

    private static final String VACATION_APPROVAL_ID_PREFIX = "VAC";

    public static Map<String, Object> createVacationApprovalBody(VacationApprovalForm form) {
        String confirmDocumentId = VACATION_APPROVAL_ID_PREFIX + form.getVacationId();

        Map<String, Object> payload = new HashMap<>();

        payload.put("approval_status", form.getApprovalStatus());
        payload.put("requester_id", form.getRequesterId());
        payload.put("vacation_date", form.getVacationDate());
        payload.put("company_id", form.getCompanyId());
        payload.put("department_id", form.getDepartmentId());
        payload.put("confirm_document_id", confirmDocumentId);
        return payload;
    }

}
