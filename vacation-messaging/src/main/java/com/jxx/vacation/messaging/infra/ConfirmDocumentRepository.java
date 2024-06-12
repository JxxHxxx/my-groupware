package com.jxx.vacation.messaging.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmContentModel;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class ConfirmDocumentRepository {

    @Qualifier(value = "approvalNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate approvalJdbcTemplate; // 결재서버 DataSource
    private final ObjectMapper objectMapper;

    public ConfirmDocumentRepository(NamedParameterJdbcTemplate approvalJdbcTemplate, ObjectMapper objectMapper) {
        this.approvalJdbcTemplate = approvalJdbcTemplate;
        this.objectMapper = objectMapper.registerModule(new JavaTimeModule());
    }

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
                "DEPARTMENT_NAME, " +
                "REQUESTER_ID, " +
                "REQUESTER_NAME, " +
                "APPROVAL_LINE_LIFE_CYCLE, " +
                "CONFIRM_DOCUMENT_CONTENT_PK) VALUES " +
                "(:confirmStatus, " +
                ":createSystem, " +
                ":createTime, " +
                ":confirmDocumentId, " +
                ":documentType, " +
                ":companyId, " +
                ":departmentId, " +
                ":departmentName, " +
                ":requesterId, " +
                ":requesterName, " +
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

    public void updateContent(VacationConfirmContentModel model) throws JsonProcessingException {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("delegatorId", model.getDelegatorId());
        parameters.addValue("delegatorName", model.getDelegatorName());
        parameters.addValue("reason", model.getReason());
        String s = objectMapper.writeValueAsString(model.getVacationDurations());
        parameters.addValue("vacationDurations", s);
        // 이거 임시코드임
        parameters.addValue("confirmDocumentContentPk", Long.valueOf(model.getDepartmentId()));

        String sql = "UPDATE JXX_CONFIRM_DOCUMENT_CONTENT_MASTER CDCM SET " +
                "CDCM.CONTENTS = JSON_REPLACE(CDCM.CONTENTS, " +
                "'$.delegator_id', :delegatorId, " +
                "'$.delegator_name', :delegatorName, " +
                "'$.reason', :reason, " +
                "'$.vacation_durations', :vacationDurations)  " +
                "WHERE CDCM.CONFIRM_DOCUMENT_CONTENT_PK  = :confirmDocumentContentPk;";

        approvalJdbcTemplate.update(sql, parameters);
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
                "DEPARTMENT_NAME, " +
                "REQUESTER_ID, " +
                "REQUESTER_NAME, " +
                "CONFIRM_DOCUMENT_CONTENT_PK " +
                "FROM JXX_CONFIRM_DOCUMENT_MASTER JCDM WHERE JCDM.CONFIRM_DOCUMENT_ID =(:confirmDocumentId)";

        RowMapper<VacationConfirmModel> rowMapper = (rs, rowNum) -> {
            String confirmStatus = rs.getString("CONFIRM_STATUS");
            String createSystem = rs.getString("CREATE_SYSTEM");
            LocalDateTime createTime = LocalDateTime.parse(rs.getString("CREATE_TIME"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            String _confirmDocumentId = rs.getString("CONFIRM_DOCUMENT_ID");
            String documentType = rs.getString("DOCUMENT_TYPE");
            String departmentId = rs.getString("DEPARTMENT_ID");
            String departmentName = rs.getString("DEPARTMENT_NAME");
            String companyId = rs.getString("COMPANY_ID");
            String requesterId = rs.getString("REQUESTER_ID");
            String requesterName = rs.getString("REQUESTER_NAME");
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
                    departmentName,
                    requesterId,
                    requesterName,
                    approvalLineCycle,
                    contentPk);
        };
        return approvalJdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    // 테스트 환경에서 에러 발생함
    public VacationConfirmContentModel findById(Long confirmDocumentContentPk) {
        String sql = "SELECT CONFIRM_DOCUMENT_CONTENT_PK, " +
                "CONTENTS " +
                "FROM JXX_CONFIRM_DOCUMENT_CONTENT_MASTER JCDCM " +
                "WHERE JCDCM.CONFIRM_DOCUMENT_CONTENT_PK =(:confirmDocumentContentPk)";
        Map<String, Object> params = new HashMap<>();
        params.put("confirmDocumentContentPk", confirmDocumentContentPk);

        return approvalJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            VacationConfirmContentModel contents = null;
            try {
                contents = objectMapper.readValue(rs.getString("CONTENTS"), VacationConfirmContentModel.class);
            } catch (JsonProcessingException e) {
                log.error("메시지 역직렬화 중 에러 발생 {}", e.getMessage(), e);
            }
            return contents;
        });
    }

    public boolean checkExist(Long confirmDocumentContentPk) {
        String sql = "SELECT CONFIRM_DOCUMENT_CONTENT_PK, " +
                "FROM JXX_CONFIRM_DOCUMENT_CONTENT_MASTER JCDCM " +
                "WHERE JCDCM.CONFIRM_DOCUMENT_CONTENT_PK =(:confirmDocumentContentPk)";
        Map<String, Object> params = new HashMap<>();
        params.put("confirmDocumentContentPk", confirmDocumentContentPk);

        RowMapper<Long> rowMapper = (rs, rowNum) -> rs.getLong("CONFIRM_DOCUMENT_CONTENT_PK");
        Long findConfirmDocumentContentPk = approvalJdbcTemplate.queryForObject(sql, params, rowMapper);
        return findConfirmDocumentContentPk == 0  ? false : true;
    }
}

