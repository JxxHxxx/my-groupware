package com.jxx.groupware.messaging.application;

import com.jxx.groupware.core.messaging.domain.queue.MessageDestination;
import com.jxx.groupware.core.messaging.domain.queue.MessageProcessType;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;


@Slf4j
@Service(value = "RdbMessageService")
public class RdbMessageService implements MessageService<MessageQ>{

    private final Map<String, DataSource> destinationDataSourceMap;

    public RdbMessageService(@Qualifier("destinationDataSourceMap") Map<String, DataSource> destinationDataSourceMap) {
        this.destinationDataSourceMap = destinationDataSourceMap;
    }

    @Override
    public void process(Message<MessageQ> message) {
        processMessage(message);
    }

    @Override
    public void retry(Message<MessageQ> message) {
        processMessage(message);
    }

    private void processMessage(Message<MessageQ> message)  {
        MessageQ payload = message.getPayload();
        // DB Bean 선택
        MessageDestination messageDestination = payload.getMessageDestination();
        Set<String> dbid = destinationDataSourceMap.keySet();
        log.info("dbid {} mdname {}", dbid, messageDestination.name());
        DataSource dataSource = destinationDataSourceMap.get(messageDestination.name());
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        template.update("INSERT INTO NOTIFICATION_TEST (MEMBER_ID, CONTENT) VALUES ('U00002', '테스트2')", Map.of());

//        try {
//            connection = dataSource.getConnection();
//            Statement statement = connection.createStatement();
//            boolean execute = statement.execute("INSERT INTO NOTIFICATION_TEST (MEMBER_ID, CONTENT) VALUES ('U00002', '테스트2')");
//            connection.close();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }


        // 유형 처리
//        MessageProcessType messageProcessType = payload.getMessageProcessType();
//        switch (messageProcessType) {
//            case INSERT -> {
//                log.info("START INSERT");
//            }
//
//            default -> {
//                log.error("MessageProcessType 이 설정되지 않았습니다.");
//            }
//        }
        // INSERT
        // UPDATE

        // DELETE - SOFT

        // DELETE - HARD
    }


}
