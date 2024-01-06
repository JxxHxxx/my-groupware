package com.jxx.vacation.messaging.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

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
    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
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

    @Bean(name = "approvalNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate approvalNamedParameterJdbcTemplate() {
        DataSource dataSource = approvalDataSource();
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "approvalDataSource")
    public DataSource approvalDataSource() {
        return DataSourceBuilder.create()
                .url(approvalDbUrl)
                .username(approvalDbUsername)
                .password(approvalDbPassword)
                .driverClassName(approvalDbDriverClassName)
                .build();
    }
}
