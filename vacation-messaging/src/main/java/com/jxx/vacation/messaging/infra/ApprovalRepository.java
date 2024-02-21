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

    public void insert(VacationConfirmModel form) {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(form);
        String sql = "INSERT INTO JXX_CONFIRM_DOCUMENT_MASTER_V2" +
                "(CONFIRM_STATUS, CREATE_SYSTEM, CREATE_TIME, CONFIRM_DOCUMENT_ID, DOCUMENT_TYPE, COMPANY_ID, DEPARTMENT_ID, REQUESTER_ID) VALUES " +
                "(:confirmStatus, :createSystem, :createTime, :confirmDocumentId, :documentType, :companyId, :departmentId, :requesterId)";

        approvalJdbcTemplate.update(sql, source);
    }

}


