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
    public String insert(InsertBuilderParameter parameter) {
        if (sqlQueryValidator.notValid(parameter)) {
            throw new UnProcessableException("처리할 수 없습니다.");
        };

        String tableName = parameter.tableName();

        StringBuilder columnsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();

        Map<String, String> requestParams = parameter.requestParams();

        List<String> columnNames = parameter.columnNames();
        Iterator<String> columnNamesIter = columnNames.iterator();

        int columnSize = columnNames.size();
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


        String columnClause = columnsBuilder.toString();
        String valueClause = valuesBuilder.toString();

        return String.format("INSERT INTO %s ( %s ) VALUES (%s)", tableName, columnClause, valueClause);
    }

    @Override
    public String update(UpdateBuilderParameter parameter) {
        // 검증 방식 구상해야함

        String tableName = parameter.tableName();

        StringBuilder setBuilder = new StringBuilder();
        StringBuilder whereBuilder = new StringBuilder();

        Map<String, String> requestParams = parameter.requestParams();
        // columnNames 기준으로 루프를 타는 이유는 클라이언트가 컬럼 매핑에 등록되지 않은 컬럼을 변경하는 것을 방지하기 위한 목적
        List<String> columnNames = parameter.columnNames();
        Iterator<String> columnNamesIter = columnNames.iterator();

        // set 절 처리
        int columnSize = columnNames.size();
        int setRoofCnt = 0;
        while (columnSize > setRoofCnt) {
            String column = columnNamesIter.next();
            String value = requestParams.get(column);

            if (Objects.nonNull(value)) {
                setBuilder.append(column)
                        .append("=")
                        .append(formatValue(value));
            }
            else {
                log.trace("column {} value is {} so, don't append column and value to the set clause", column, value);
            }

            // 구분자 처리
            if (columnNamesIter.hasNext() && !setBuilder.isEmpty()) {
                setBuilder.append(", ");
            }
            setRoofCnt++;
        }

        // where 절 처리
        Map<String, String> whereClauseParams = parameter.whereClauseParams();

        Iterator<Map.Entry<String, String>> whereClauseIterator = null;
        try {
            whereClauseIterator = whereClauseParams.entrySet().iterator();
        } catch (NullPointerException e) {
            log.error("MessageQ - Content에 whereClause 이 없는 것으로 보입니다.", e);
            throw new RdbMessagePolicyException("WHERE 절 조건이 존재하지 않습니다.");
        }

        // whereClause 를 가져오긴 했는데 비어 있을 경우
        if (whereClauseParams.isEmpty()) {
            throw new RdbMessagePolicyException("WHERE 절 조건이 존재하지 않습니다.");
        }

        int whereRoofCnt = 0;
        int whereClauseSize = whereClauseParams.size();
        while (whereClauseSize > whereRoofCnt) {
            Map.Entry<String, String> whereCaluseEntry = whereClauseIterator.next();

            String column = whereCaluseEntry.getKey();
            String value = whereCaluseEntry.getValue();

            if (Objects.nonNull(value)) {
                whereBuilder.append(column)
                        .append("=")
                        .append(formatValue(value));
            }
            else {
                log.trace("column {} value is {} so, don't append column and value to the where clause", column, value);
            }

            // where 절 조건이 추가로 존재
            // where 절 Query 가 존재할 때 (Objects.nonNull) 처리로 없을 수 있음
            if (whereClauseIterator.hasNext() && !whereBuilder.isEmpty()) {
                whereBuilder.append(" AND ");
            }

            whereRoofCnt++;
        }

        String setClause = setBuilder.toString();
        String whereClause = whereBuilder.toString();

        return String.format("UPDATE %s SET %s WHERE %s", tableName, setClause, whereClause);
    }

    @Override
    public String delete(DeleteBuilderParameter parameter) {
        // 검증 방식 구상해야함

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
}
