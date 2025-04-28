package com.jxx.groupware.messaging.application.sql.builder;


import com.jxx.groupware.core.messaging.domain.UnProcessableException;
import com.jxx.groupware.messaging.application.sql.validate.SimpleSqlQueryValidator;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class SimpleSqlQueryBuilderTest {

    SimpleSqlQueryBuilder sqlQueryBuilder;

    public SimpleSqlQueryBuilderTest() {
        this.sqlQueryBuilder = new SimpleSqlQueryBuilder(new SimpleSqlQueryValidator());
    }

    @DisplayName("requestParams 의 keySet 과 columnNames 리스트는 동일해야 합니다. " +
            "다시 말해 columnNames 의 모든 값들과 requestParams keySet 모든 값들은 일치해야 합니다.")
    @Test
    void insert_success_case() {
        String tableName = "NOTIFICATION_TEST";

        List<String> columnNames = List.of("MEMBER_ID", "WEIGHT", "CONTENT", "AGE");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("CONTENT", "개인정보");
        requestParams.put("AGE", "30");
        requestParams.put("WEIGHT", "29.3");
        requestParams.put("MEMBER_ID", "JxxHxxx"); // columnNames 등록되지 않은 정보

        InsertBuilderParameter parameter = new InsertBuilderParameter(tableName, columnNames, requestParams);

        assertThatCode(() -> sqlQueryBuilder.insert(parameter))
                .doesNotThrowAnyException();

        // 로그 출력을 위한 편의 테스트
        String insertQuery = sqlQueryBuilder.insert(parameter);
        log.info("insertQuery {}", insertQuery);
    }

    @DisplayName("requestParams key 들과 columnNames 값들은 동일해야 한다. " +
            "1. requestParams 에만 존재하는 값이 있을 경우" +
            "UnProcessableException 예외가 발생한다.")
    @Test
    void insert_fail_case1() {
        String tableName = "NOTIFICATION_TEST";

        List<String> columnNames = List.of("MEMBER_ID", "WEIGHT", "CONTENT", "AGE");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("CONTENT", "개인정보");
        requestParams.put("AGE", "30");
        requestParams.put("WEIGHT", "29.3");
        requestParams.put("MEMBER_ID", "JxxHxxx");
        requestParams.put("ORG_NAME", "IT Service"); // columnNames 등록되지 않은 key

        InsertBuilderParameter parameter = new InsertBuilderParameter(tableName, columnNames, requestParams);

        Assertions.assertThatThrownBy(() -> sqlQueryBuilder.insert(parameter))
                .isInstanceOf(UnProcessableException.class);
    }

    @DisplayName("requestParams key 들과 columnNames 값들은 동일해야 한다. " +
            "2. columnNames 에만 존재하는 값이 있을 경우" +
            "UnProcessableException 예외가 발생한다.")
    @Test
    void insert_fail_case2() {
        String tableName = "NOTIFICATION_TEST";

        List<String> columnNames = List.of("MEMBER_ID", "WEIGHT", "CONTENT", "AGE");

        // MEMBER_ID 누락
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("CONTENT", "개인정보");
        requestParams.put("AGE", "30");
        requestParams.put("WEIGHT", "29.3");

        InsertBuilderParameter parameter = new InsertBuilderParameter(tableName, columnNames, requestParams);

        Assertions.assertThatThrownBy(() -> sqlQueryBuilder.insert(parameter))
                .isInstanceOf(UnProcessableException.class);
    }

    @DisplayName("UPDATE Query 문을 만드는 로직을 테스트 한다." +
            "1. columnNames 는 DB에 저장된 컬럼 매핑 정보로 requestParams 에 컬럼 매핑에 등록된 컬럼 값이 아닐 경우에 반응하지 않는다." +
            "다시 말해 requestParams 에 들어온 ORG_NAME 은 UPDATE 문 생성 시 고려되지 않는다." +
            "2. columnNames 는 포함되지만 requestParams 에 포함되지 않는 컬럼 값들은 SET 절에 포함되지 않는다. " +
            "정확히는 requestParams 에서 찾은 키 값이 null 일때 반응하지 않는다. ")
    @Test
    void update_success_case() {
        String tableName = "NOTIFICATION_TEST";

        List<String> columnNames = List.of("MEMBER_ID", "WEIGHT", "CONTENT", "AGE");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("CONTENT", "개인정보");
        requestParams.put("AGE", "30");
        requestParams.put("WEIGHT", "29.3");
        requestParams.put("ORG_NAME", "IT센터"); // columnNames 등록되지 않은 정보

        Map<String, String> whereClause = new HashMap<>();
        whereClause.put("MEMBER_ID", "U00001");

        UpdateBuilderParameter parameter = new UpdateBuilderParameter(tableName, columnNames, requestParams, whereClause);
        String updateQuery = sqlQueryBuilder.update(parameter);
        log.info("updateQuery {}", updateQuery);
        assertThat(updateQuery)
                .isEqualTo("UPDATE NOTIFICATION_TEST SET WEIGHT=29.3, CONTENT='개인정보', AGE=30 WHERE MEMBER_ID='U00001'");
    }

    @DisplayName("whereClauseParams 가 존재하지 않을 경우 RdbMessagePolicyException 예외를 던진다")
    @Test
    void update_fail_case1() {
        String tableName = "NOTIFICATION_TEST";

        List<String> columnNames = List.of("MEMBER_ID", "WEIGHT", "CONTENT", "AGE");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("CONTENT", "개인정보");
        requestParams.put("AGE", "30");
        requestParams.put("WEIGHT", "29.3");
        requestParams.put("ORG_NAME", "IT센터"); // columnNames 등록되지 않은 정보


        UpdateBuilderParameter parameter = new UpdateBuilderParameter(tableName, columnNames, requestParams, null);

        assertThatThrownBy(() -> sqlQueryBuilder.update(parameter))
                .isInstanceOf(RdbMessagePolicyException.class);
    }

    @DisplayName("whereClauseParams 에 어떤 WHERE 조건도 존재하지 않을 경우 RdbMessagePolicyException 예외를 던진다")
    @Test
    void update_fail_case2() {
        String tableName = "NOTIFICATION_TEST";

        List<String> columnNames = List.of("MEMBER_ID", "WEIGHT", "CONTENT", "AGE");

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("CONTENT", "개인정보");
        requestParams.put("AGE", "30");
        requestParams.put("WEIGHT", "29.3");
        requestParams.put("ORG_NAME", "IT센터"); // columnNames 등록되지 않은 정보

        Map<String, String> whereClause = new HashMap<>();

        UpdateBuilderParameter parameter = new UpdateBuilderParameter(tableName, columnNames, requestParams, whereClause);

        assertThatThrownBy(() -> sqlQueryBuilder.update(parameter))
                .isInstanceOf(RdbMessagePolicyException.class);
    }
}