package com.jxx.vacation.messaging.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.vacation.core.message.MessageBodyBuilder;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmContentModel;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmMessageForm;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmModel;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationDurationModel;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

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
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/third-party.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void insert_success_case() throws JsonProcessingException {
        VacationConfirmMessageForm messageForm = VacationConfirmMessageForm.create(
                "TESTER_ID",
                "TEST_CP",
                "TEST_DP",
                2l,
                123L,
                "휴가신청서",
                "TEST_DELEGATOR",
                "개인사정",
                "TESTERNAME",
                "TESTDPNAME",
                List.of(new VacationDurationModel(LocalDateTime.now(), LocalDateTime.now()))
        );
        Map<String, Object> messageBody = MessageBodyBuilder.from(messageForm);
        VacationConfirmModel vacationConfirmModel = VacationConfirmModel.from(messageBody);
        //when
        VacationConfirmContentModel vacationConfirmContentModel = VacationConfirmContentModel.from(messageBody);
        Long pk = confirmDocumentRepository.insertContent(vacationConfirmContentModel);
        confirmDocumentRepository.insert(pk, vacationConfirmModel);

        String confirmDocumentId = vacationConfirmModel.getConfirmDocumentId();
        VacationConfirmModel findVacationConfirmModel = confirmDocumentRepository.findById(confirmDocumentId);
        assertThat(findVacationConfirmModel).isNotNull();
        VacationConfirmContentModel contentModel = confirmDocumentRepository.findById(vacationConfirmModel.getContentPk());
        assertThat(contentModel.getReason()).isEqualTo("휴가신청서");
    }
}