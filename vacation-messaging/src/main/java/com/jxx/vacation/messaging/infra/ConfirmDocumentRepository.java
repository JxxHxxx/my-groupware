package com.jxx.vacation.messaging.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmContentModel;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ConfirmDocumentRepository {

    @Qualifier(value = "approvalNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate approvalJdbcTemplate; // 결재서버 DataSource

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
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapper timeModuleObjectMapper = objectMapper.registerModule(new JavaTimeModule());
        parameters.addValue("contents", timeModuleObjectMapper.writeValueAsString(model.toMap()));

        String sql = "INSERT INTO JXX_CONFIRM_DOCUMENT_CONTENT_MASTER " +
                "(CONTENTS) VALUES (:contents)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        approvalJdbcTemplate.update(sql, parameters, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public VacationConfirmModel findById(String confirmDocumentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("confirmDocumentId", confirmDocumentId);

        String sql = "SELECT " +
                "CONFIRM_DOCUMENT_PK, " +
                "APPROVAL_LINE_LIFE_CYCLE, " +
                "CONFIRM_STATUS, " +
                "CREATE_SYSTEM, " +
                "CREATE_TIME, " +
                "CONFIRM_DOCUMENT_ID, " +
                "DOCUMENT_TYPE, " +
                "COMPANY_ID, " +
                "DEPARTMENT_ID, " +
                "REQUESTER_ID, " +
                "CONFIRM_DOCUMENT_CONTENT_PK " +
                "FROM JXX_CONFIRM_DOCUMENT_MASTER JCDM WHERE JCDM.CONFIRM_DOCUMENT_ID =(:confirmDocumentId)";

        RowMapper<VacationConfirmModel> rowMapper = (rs, rowNum) -> {
            String confirmStatus = rs.getString("CONFIRM_STATUS");
            String createSystem = rs.getString("CREATE_SYSTEM");
            LocalDateTime createTime = LocalDateTime.parse(rs.getString("CREATE_TIME"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            String _confirmDocumentId = rs.getString("CONFIRM_DOCUMENT_ID");
            String documentType = rs.getString("DOCUMENT_TYPE");
            String departmentId = rs.getString("DEPARTMENT_ID");
            String companyId = rs.getString("COMPANY_ID");
            String requesterId = rs.getString("REQUESTER_ID");
            String approvalLineCycle = rs.getString("APPROVAL_LINE_LIFE_CYCLE");
            Long contentPk = rs.getLong("CONFIRM_DOCUMENT_CONTENT_PK");

            return new VacationConfirmModel(
                    confirmStatus,
                    createSystem,
                    createTime,
                    _confirmDocumentId,
                    documentType,
                    companyId,
                    departmentId,
                    requesterId,
                    approvalLineCycle,
                    contentPk);
        };
        return approvalJdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    public VacationConfirmContentModel findById(Long confirmDocumentContentPk) {
        String sql = "SELECT CONFIRM_DOCUMENT_CONTENT_PK, " +
                "CONTENTS " +
                "FROM JXX_CONFIRM_DOCUMENT_CONTENT_MASTER JCDCM " +
                "WHERE JCDCM.CONFIRM_DOCUMENT_CONTENT_PK =(:confirmDocumentContentPk)";
        Map<String, Object> params = new HashMap<>();
        params.put("confirmDocumentContentPk", confirmDocumentContentPk);

        RowMapper<VacationConfirmContentModel> rowMapper = (rs, rowNum) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            VacationConfirmContentModel contents = null;
            try {
                contents = objectMapper.readValue(rs.getString("CONTENTS"), VacationConfirmContentModel.class);
            } catch (JsonProcessingException e) {
            }
            System.out.println("contents" + contents);

            return contents;
        };
        return approvalJdbcTemplate.queryForObject(sql, params, rowMapper);
    }
}

//}

//            return new VacationConfirmContentModel(
//                    String.valueOf(contents.get("title")),
//                    String.valueOf(contents.get("delegator_id")),
//                    String.valueOf(contents.get("reason")),
//                    String.valueOf(contents.get("requester_id")),
//                    String.valueOf(contents.get("requester_name")),
//                    String.valueOf(contents.get("department_id")),
//                    String.valueOf(contents.get("department_name")),
//                    (ArrayList) contents.get("vacation_durations"));
//        };

