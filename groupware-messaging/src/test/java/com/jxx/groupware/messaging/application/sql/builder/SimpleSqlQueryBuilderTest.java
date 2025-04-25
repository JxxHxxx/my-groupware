package com.jxx.groupware.messaging.application.sql.builder;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SimpleSqlQueryBuilderTest {

    @Test
    void update() {
        String wrapper = "UPDATE %s SET %s WHERE %s";
        String sql = String.format(wrapper, "NOTIFICATION_TEST", "MEMBER_ID='U00001', CONTENT='변경 완료'", "MEMBER_ID='U00001'");
        log.info("sql {}", sql);
    }
}