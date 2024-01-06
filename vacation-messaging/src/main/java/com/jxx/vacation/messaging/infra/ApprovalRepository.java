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
        String sql = "INSERT INTO JXX_CONFIRM_DOCUMENT_MASTER (COMPANY_ID, DEPARTMENT_ID, CREATE_SYSTEM, CONFIRM_STATUS, DOCUMENT_TYPE, APPROVAL_ID) VALUES " +
                "(:companyId, :departmentId, :createSystem, :confirmStatus, :documentType, :approvalId) ";

        approvalJdbcTemplate.update(sql, source);
    }

}


