package com.jxx.vacation.messaging.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApprovalRepository {

    @Qualifier(value = "approvalNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate approvalJdbcTemplate;

    public void insert(VacationConfirmForm form) {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(form);
        String sql = "INSERT INTO JXX_CONFIRM_DOCUMENT_MASTER " +
                "(COMPANY_ID, CONFIRM_DOCUMENT_ID, CONFIRM_STATUS, CREATE_SYSTEM, DEPARTMENT_ID, DOCUMENT_TYPE, REQUESTER_ID) VALUES " +
                "(:companyId, :confirmDocumentId,  :confirmStatus, :createSystem, :departmentId, :documentType, :requesterId) ";

        approvalJdbcTemplate.update(sql, source);
    }

}


