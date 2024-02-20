package com.jxx.vacation.core.message;

import com.jxx.vacation.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.vacation.core.message.payload.approval.form.VacationApprovalForm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// SimpleMessageConsumer 같이 리팩토링하면 변경 포인트 하나 줄일 수 있어보임
public class MessageBodyBuilder {

    public static Map<String, Object> createVacationApprovalBody(VacationApprovalForm form) {
        String vacationConfirmDocumentId = ConfirmDocumentIdGenerator.execute(form.getCompanyId(), form.getVacationId());
        Map<String, Object> payload = new HashMap<>();

        payload.put("confirm_status", form.getConfirmStatus());
        payload.put("requester_id", form.getRequesterId());
        payload.put("vacation_date", form.getVacationDate());
        payload.put("create_system", form.getCreateSystem());
        payload.put("document_type", form.getDocumentType());
        payload.put("company_id", form.getCompanyId());
        payload.put("department_id", form.getDepartmentId());
        payload.put("confirm_document_id", vacationConfirmDocumentId);
        payload.put("create_time", form.getCreateTime());
        return payload;
    }
}
