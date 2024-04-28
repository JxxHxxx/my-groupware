package com.jxx.vacation.messaging.application;

import com.jxx.vacation.core.message.MessageBodyBuilder;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmMessageForm;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationConfirmModel;
import com.jxx.vacation.core.message.body.vendor.confirm.VacationDurationModel;
import com.jxx.vacation.core.message.domain.MessageDestination;
import com.jxx.vacation.core.message.domain.MessageProcessStatus;
import com.jxx.vacation.core.message.domain.MessageQ;
import com.jxx.vacation.core.message.domain.MessageQResult;
import com.jxx.vacation.core.message.infra.MessageQRepository;
import com.jxx.vacation.core.message.infra.MessageQResultRepository;
import com.jxx.vacation.messaging.infra.ConfirmDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * WAS 를 띄우기 때문에 폴링이 자동으로 이뤄진다. 테스트 설정에서 폴링 주기를
 */

@Slf4j
@SpringBootTest
class VacationMessageServiceTest {

    @Autowired
    DataSource dataSource;
    @Autowired
    MessageQRepository messageQRepository;
    @Autowired
    VacationMessageService vacationMessageService;
    @Autowired
    MessageQResultRepository messageQResultRepository;
    @Autowired
    ConfirmDocumentRepository confirmDocumentRepository;

    @BeforeEach
    void init() {
        try {
            Connection connection = dataSource.getConnection();
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/third-party.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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

        MessageQ messageQ = MessageQ.builder()
                .messageProcessStatus(MessageProcessStatus.SENT)
                .messageDestination(MessageDestination.APPROVAL)
                .body(messageBody)
                .retryId(null)
                .build();
        messageQRepository.save(messageQ);
    }

    @Test
    void process_success_case() {
        List<MessageQ> messageQs = messageQRepository.findWithLimit(1);
        MessageQ messageQ = messageQs.get(0);
        Message<MessageQ> message = MessageBuilder.withPayload(messageQ)
                .build();

        String confirmDocumentId = String.valueOf(messageQ.getBody().get("confirm_document_id"));

        vacationMessageService.process(message);
        VacationConfirmModel vacationConfirmModel = confirmDocumentRepository.findById(confirmDocumentId);
        MessageQResult messageQResult = messageQResultRepository.findById(messageQ.getPk()).get();
        Assertions.assertThat(messageQResult.getMessageProcessStatus()).isEqualTo(MessageProcessStatus.SUCCESS);

        Assertions.assertThat(vacationConfirmModel.getRequesterId()).isEqualTo("TESTER_ID");
    }

}