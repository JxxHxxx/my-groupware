package com.jxx.groupware.core.message;

import com.jxx.groupware.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.groupware.core.message.body.vendor.confirm.CommonVacationConfirmMessageForm;
import com.jxx.groupware.core.message.body.vendor.confirm.VacationConfirmMessageForm;
import com.jxx.groupware.core.message.body.vendor.confirm.VacationUpdateMessageForm;

import java.util.HashMap;
import java.util.Map;

public class MessageBodyBuilder {
    public static Map<String, Object> from(VacationConfirmMessageForm messageForm) {
        String vacationConfirmDocumentId = ConfirmDocumentIdGenerator.execute(messageForm.getCompanyId(), messageForm.getVacationId());
        Map<String, Object> payload = new HashMap<>();

        payload.put("confirm_status", messageForm.getConfirmStatus());
        payload.put("requester_id", messageForm.getRequesterId());
        payload.put("vacation_date", messageForm.getVacationDate());
        payload.put("create_system", messageForm.getCreateSystem());
        payload.put("document_type", messageForm.getDocumentType());
        payload.put("company_id", messageForm.getCompanyId());
        payload.put("department_id", messageForm.getDepartmentId());
        payload.put("confirm_document_id", vacationConfirmDocumentId);
        payload.put("create_time", messageForm.getCreateTime());
        payload.put("approval_line_life_cycle", messageForm.getApprovalLineLifeCycle());
        payload.put("title", messageForm.getTitle());
        payload.put("delegator_id", messageForm.getDelegatorId());
        payload.put("delegator_name", messageForm.getDelegatorName());
        payload.put("reason", messageForm.getReason());
        payload.put("requester_name", messageForm.getRequesterName());
        payload.put("department_name", messageForm.getDepartmentName());
        payload.put("vacation_durations", messageForm.getVacationDurations());
        return payload;
    }

    public static Map<String, Object> from(CommonVacationConfirmMessageForm messageForm) {
        String vacationConfirmDocumentId = ConfirmDocumentIdGenerator.execute(messageForm.getCompanyId(), messageForm.getVacationId());
        Map<String, Object> payload = new HashMap<>();

        payload.put("confirm_status", messageForm.getConfirmStatus());
        payload.put("requester_id", messageForm.getRequesterId());
        payload.put("vacation_date", messageForm.getVacationDate());
        payload.put("create_system", messageForm.getCreateSystem());
        payload.put("document_type", messageForm.getDocumentType());
        payload.put("company_id", messageForm.getCompanyId());
        payload.put("department_id", messageForm.getDepartmentId());
        payload.put("confirm_document_id", vacationConfirmDocumentId);
        payload.put("create_time", messageForm.getCreateTime());
        payload.put("approval_line_life_cycle", messageForm.getApprovalLineLifeCycle());
        return payload;
    }

    public static Map<String, Object> from(VacationUpdateMessageForm messageForm) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("delegator_id", messageForm.getDelegatorId());
        payload.put("delegator_name", messageForm.getDelegatorName());
        payload.put("reason", messageForm.getReason());
        payload.put("vacation_durations", messageForm.getVacationDurations());
        payload.put("content_pk", messageForm.getContentPk());
        return payload;
    }
}
