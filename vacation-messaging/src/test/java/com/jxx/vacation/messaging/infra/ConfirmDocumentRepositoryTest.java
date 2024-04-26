package com.jxx.vacation.messaging.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmContentModel;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfirmDocumentRepositoryTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ConfirmDocumentRepository confirmDocumentRepository;
    @Autowired
    @Qualifier(value = "approvalNamedParameterJdbcTemplate")
    NamedParameterJdbcTemplate approvalJdbcTemplate;

    @Autowired
    @Qualifier(value = "approvalDataSource")
    DataSource dataSource;
    @BeforeAll
    void init() {
        try {
            Connection connection = dataSource.getConnection();
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/test.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void test() throws JsonProcessingException {
    }
}