package com.jxx.groupware.messaging.application.sql.builder;

import com.jxx.groupware.core.messaging.domain.UnProcessableException;
import com.jxx.groupware.messaging.application.sql.validate.SqlQueryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleSqlQueryBuilder implements SqlQueryBuilder {

    private final SqlQueryValidator sqlQueryValidator;

    @Override
    public String insert(QueryBuilderParameter parameter) {
        if (sqlQueryValidator.notValid(parameter)) {
            throw new UnProcessableException("처리할 수 없습니다.");
        };

        String tableName = parameter.tableName();

        StringBuilder columnsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();

        Map<String, String> requestParams = parameter.requestParams();

        List<String> columnNames = parameter.columnNames();
        Iterator<String> columnNamesIter = columnNames.iterator();

        int columnSize = columnNames.size(); // 3
        int roofCnt = 0;
        while (columnSize > roofCnt) {
            String column = columnNamesIter.next();
            String requestValue = requestParams.get(column);

            columnsBuilder.append(column);
            valuesBuilder.append(formatValue(requestValue));

            // 구분자 처리
            if (columnNamesIter.hasNext()) {
                columnsBuilder.append(", ");
                valuesBuilder.append(", ");
            }
            roofCnt++;
        }


        String columnsAssembleResult = columnsBuilder.toString();
        String valuesAssembleResult = valuesBuilder.toString();

        return String.format("INSERT INTO %s ( %s ) VALUES (%s)", tableName, columnsAssembleResult, valuesAssembleResult);
    }

    @Override
    public String update(QueryBuilderParameter parameter) {
        return null;
    }

    @Override
    public String delete(QueryBuilderParameter parameter) {
        return null;
    }

    private static String formatValue(String val) {
        return isNumeric(val) ? val : "'" + val + "'";
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
    // 쿼리 생성 전 검증 로직
    private boolean valid() {
        return true;
    }
}
