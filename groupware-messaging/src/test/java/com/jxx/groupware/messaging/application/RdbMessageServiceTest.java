package com.jxx.groupware.messaging.application;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class RdbMessageServiceTest {
    @Test
    void 쿼리_조립_테스트() {
        List<String> columns = List.of("MEMBER_ID", "AGE", "USER_NAME");
        String tableName = "NOTIFICATION_TEST";

        Map<String, String> params = new HashMap<>();
        params.put("MEMBER_ID", "JxxHxxx");
        params.put("AGE", "25");
        params.put("USER_NAME", "재헌");

        StringBuilder colsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();

        for (int i = 0; i < columns.size(); i++) {
            String col = columns.get(i);
            String val = params.get(col);

            colsBuilder.append(col);
            valuesBuilder.append(formatValue(val));

            if (i < columns.size() - 1) {
                colsBuilder.append(", ");
                valuesBuilder.append(", ");
            }
        }

        String cols = colsBuilder.toString();
        String values = valuesBuilder.toString();

        String sql = String.format("INSERT INTO %s ( %s ) VALUES (%s)", tableName, cols, values);
        log.info("sql {}", sql);

    }

    private static String formatValue(String val) {
        if (isNumeric(val)) {
            return val; // 숫자는 그냥 출력
        } else {
            return "'" + val + "'"; // 문자열은 따옴표로 감쌈
        }
    }

    private static boolean isNumeric(String val) {
        if (val == null) return false;
        try {
            Double.parseDouble(val);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
