package com.jxx.groupware.messaging.application.sql.builder;

import java.util.List;
import java.util.Map;

/**
 * @param tableName UPDATE 대상이 되는 테이블 명 / JXX_MESSAGE_TABLE_MAPPING 테이블의 SERVICE_ID 에 의해 결정
 * @param columnNames 메시징 시스템 DB에 등록되어 있는 UPDATE 대상이 되는 컬럼 List / JXX_MESSAGE_COLUMN_MAPPING 테이블 SERVICE_ID 에 의해 결정
 * @param requestParams 클라이언트로 부터 받은 SET 절 대상이 되는 컬럼 List
 * @param whereClauseParams 클라이언트로 부터 받은 WHERE 절 대상이 되는 컬럼 List
 **/
public record UpdateBuilderParameter(
        String tableName,
        List<String> columnNames,
        Map<String, String> requestParams,
        Map<String, String> whereClauseParams
) {
}
