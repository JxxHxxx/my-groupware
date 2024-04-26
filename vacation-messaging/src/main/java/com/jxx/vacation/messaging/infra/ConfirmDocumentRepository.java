package com.jxx.vacation.messaging.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmContentModel;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConfirmDocumentRepository {

    @Qualifier(value = "approvalNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate approvalJdbcTemplate; // 결재서버 DataSource
    private final ObjectMapper objectMapper;

    // 컬럼 순서대로 SQL 쿼리문 짜야됨
    public void insert(Long contentPk, VacationConfirmModel model) {
        model.setContentPk(contentPk);
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(model);
        String sql = "INSERT INTO JXX_CONFIRM_DOCUMENT_MASTER" +
                "(CONFIRM_STATUS, " +
                "CREATE_SYSTEM, " +
                "CREATE_TIME, " +
                "CONFIRM_DOCUMENT_ID, " +
                "DOCUMENT_TYPE, " +
                "COMPANY_ID, " +
                "DEPARTMENT_ID, " +
                "REQUESTER_ID, " +
                "APPROVAL_LINE_LIFE_CYCLE, " +
                "CONFIRM_DOCUMENT_CONTENT_PK) VALUES " +
                "(:confirmStatus, " +
                ":createSystem, " +
                ":createTime, " +
                ":confirmDocumentId, " +
                ":documentType, " +
                ":companyId, " +
                ":departmentId, " +
                ":requesterId, " +
                ":approvalLineLifeCycle, " +
                ":contentPk)";

        approvalJdbcTemplate.update(sql, source);
    }

    public Long insertContent(VacationConfirmContentModel model) throws JsonProcessingException {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("contents", objectMapper.writeValueAsString(model.toMap()));

        String sql = "INSERT INTO JXX_CONFIRM_DOCUMENT_CONTENT_MASTER " +
                "(CONTENTS) VALUES (:contents)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        approvalJdbcTemplate.update(sql, parameters, keyHolder);
        return keyHolder.getKey().longValue();
    }

}


