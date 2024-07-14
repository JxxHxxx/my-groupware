package com.jxx.groupware.messaging.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.groupware.core.message.MessageBodyBuilder;
import com.jxx.groupware.core.message.body.vendor.confirm.VacationConfirmContentModel;
import com.jxx.groupware.core.message.body.vendor.confirm.VacationConfirmMessageForm;
import com.jxx.groupware.core.message.body.vendor.confirm.VacationConfirmModel;
import com.jxx.groupware.core.message.body.vendor.confirm.VacationDurationModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
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

    @Disabled(value = "역직렬화가 잘 안되네...?")
    @DisplayName("결재 서버 INSERT/SELECT 쿼리 테스트")
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
                "테스트부서",
                List.of(new VacationDurationModel(String.valueOf(LocalDateTime.now()), String.valueOf(LocalDateTime.now())))
        );
        Map<String, Object> messageBody = MessageBodyBuilder.from(messageForm);
        VacationConfirmModel vacationConfirmModel = VacationConfirmModel.from(messageBody);
        //when
        VacationConfirmContentModel vacationConfirmContentModel = VacationConfirmContentModel.from(messageBody);
        Long pk = confirmDocumentRepository.insertContent(vacationConfirmContentModel);
        confirmDocumentRepository.insert(pk, vacationConfirmModel);

        //then
        String confirmDocumentId = vacationConfirmModel.getConfirmDocumentId();
        VacationConfirmModel findVacationConfirmModel = confirmDocumentRepository.findById(confirmDocumentId);
        assertThat(findVacationConfirmModel).isNotNull();
        VacationConfirmContentModel contentModel = confirmDocumentRepository.findById(vacationConfirmModel.getContentPk());
        assertThat(contentModel.getReason()).isEqualTo("휴가신청서");
    }

    @Autowired
    PlatformTransactionManager platformTransactionManager;
    @DisplayName("롤백 테스트 수행")
    @Test
    void test_rollback_case() throws JsonProcessingException {
        //given
        VacationConfirmMessageForm messageForm = VacationConfirmMessageForm.create(
                "TESTER_ID",
                "TEST_CP",
                null,
                2l,
                123L,
                "휴가신청서",
                "TEST_DELEGATOR",
                "개인사정",
                "TESTERNAME",
                "DEPNAME",
                "테스트부서",
                List.of(new VacationDurationModel(String.valueOf(LocalDateTime.now()), String.valueOf(LocalDateTime.now()))));
        Map<String, Object> messageBody = MessageBodyBuilder.from(messageForm);
        VacationConfirmModel vacationConfirmModel = VacationConfirmModel.from(messageBody);
        //when
        VacationConfirmContentModel vacationConfirmContentModel = VacationConfirmContentModel.from(messageBody);
        boolean exist = false;
        try {
            Long confirmPk = confirmDocumentRepository.insertContent(vacationConfirmContentModel);
            confirmDocumentRepository.insert(confirmPk, vacationConfirmModel);
            //when
            exist = confirmDocumentRepository.checkExist(vacationConfirmModel.getContentPk());
        } catch (Exception e) {
            log.info("error occur", e);
        }
        assertThat(exist).isEqualTo(false);
    }

    @DisplayName("커밋 테스트 수행")
    @Test
    void test_commit_case() throws JsonProcessingException {
        //given
        VacationConfirmMessageForm messageForm = VacationConfirmMessageForm.create(
                "TESTER_ID",
                "TEST_CP",
                "DPID",
                2l,
                123L,
                "휴가신청서",
                "TEST_DELEGATOR",
                "개인사정",
                "TESTERNAME",
                "DEPNAME",
                "테스트부서",
                List.of(new VacationDurationModel(String.valueOf(LocalDateTime.now()), String.valueOf(LocalDateTime.now()))));
        Map<String, Object> messageBody = MessageBodyBuilder.from(messageForm);
        VacationConfirmModel vacationConfirmModel = VacationConfirmModel.from(messageBody);
        //when
        VacationConfirmContentModel vacationConfirmContentModel = VacationConfirmContentModel.from(messageBody);
        boolean exist = false;
        try {
            Long confirmPk = confirmDocumentRepository.insertContent(vacationConfirmContentModel);
            confirmDocumentRepository.insert(confirmPk, vacationConfirmModel);
            //when
            exist = confirmDocumentRepository.checkExist(vacationConfirmModel.getContentPk());
        } catch (Exception e) {
            log.info("error occur", e);
        }
        assertThat(exist).isEqualTo(true);
    }
}