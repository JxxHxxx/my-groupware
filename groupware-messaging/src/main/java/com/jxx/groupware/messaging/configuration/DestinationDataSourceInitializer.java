package com.jxx.groupware.messaging.configuration;

import com.jxx.groupware.core.messaging.domain.destination.MessageQDestination;
import com.jxx.groupware.core.messaging.infra.MessageQDestinationRepository;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class DestinationDataSourceInitializer {

    private final ApplicationContext context;
    private final MessageQDestinationRepository destinationRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void refreshContext() {
        List<MessageQDestination> destinations = destinationRepository.findAll();
        Map<String, DataSource> destinationDataSourceMap = (HashMap<String, DataSource>) context.getBean("destinationDataSourceMap");

        for (MessageQDestination destination : destinations) {
            Map<String, Object> connectionInformation = destination.getConnectionInformation();
            String url = String.valueOf(connectionInformation.get("url"));
            String password = String.valueOf(connectionInformation.get("password"));
            String username = String.valueOf(connectionInformation.get("username"));
            String driverClassName = String.valueOf(connectionInformation.get("driverClassName"));
            String destinationId = destination.getDestinationId();
            HikariDataSource dataSource = new HikariDataSource();

            dataSource.setJdbcUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName(driverClassName);
            dataSource.setPoolName("msg-" + destinationId);

            destinationDataSourceMap.put(destination.getDestinationId(), dataSource);
        }
    }
    @EventListener(ApplicationReadyEvent.class)
    public void checkDestinationDataSource() {
        Map<String, DataSource> destinationDataSourceMap = (HashMap<String, DataSource>) context.getBean("destinationDataSourceMap");

        StringBuilder sb = new StringBuilder("\n=========================================");
        for (String destinationId : destinationDataSourceMap.keySet()) {
            DataSource dataSource = destinationDataSourceMap.get(destinationId);
            try {
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT 1");
                boolean valid = rs.next();
                if (valid) {
                    log.info("{} db connect test success", destinationId);
                    sb.append("\n destionation id :" + destinationId + " connected!!");
                } else {
                    log.error("{} db connect test fail", destinationId);
                }
            } catch (SQLException e) {
                log.error("error occur");
            }
        }
        sb.append("\n=========================================");
        log.info("{}", sb);
    }
}
