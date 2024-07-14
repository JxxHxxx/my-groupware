package com.jxx.groupware.messaging.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
public class ThirdPartyDBConfiguration {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.pool-name}")
    private String poolName;
    @Value("${spring.datasource.max-pool-size}")
    private int maxPoolSize;

    // 메시지 서버 데이터 소스 설정
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setMaximumPoolSize(maxPoolSize);
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setPoolName(poolName);

        return hikariDataSource;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Value("${3rd-party.datasource.approval.url}")
    private String approvalDbUrl;
    @Value("${3rd-party.datasource.approval.username}")
    private String approvalDbUsername;
    @Value("${3rd-party.datasource.approval.password}")
    private String approvalDbPassword;
    @Value("${3rd-party.datasource.approval.driver-class-name}")
    private String approvalDbDriverClassName;
    @Value("${3rd-party.datasource.approval.pool-name}")
    private String approvalPoolName;
    @Value("${3rd-party.datasource.approval.max-pool-size}")
    private int approvalMaxPoolSize;

    @Bean(name = "transactionTemplate")
    public TransactionTemplate transactionTemplate() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(approvalDataSource());
        return new TransactionTemplate(transactionManager);
    }

    @Bean(name = "approvalNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate approvalNamedParameterJdbcTemplate() {
        DataSource dataSource = approvalDataSource();
        return new NamedParameterJdbcTemplate(dataSource);
    }

    // 결재 서버(써드 파티) 데이터 소스 설정
    @Bean(name = "approvalDataSource")
    public DataSource approvalDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setMaximumPoolSize(approvalMaxPoolSize);
        hikariDataSource.setJdbcUrl(approvalDbUrl);
        hikariDataSource.setUsername(approvalDbUsername);
        hikariDataSource.setPassword(approvalDbPassword);
        hikariDataSource.setDriverClassName(approvalDbDriverClassName);
        hikariDataSource.setPoolName(approvalPoolName);

        return hikariDataSource;
    }
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
