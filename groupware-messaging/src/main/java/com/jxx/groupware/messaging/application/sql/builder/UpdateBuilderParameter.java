package com.jxx.groupware.messaging.application.sql.builder;

import java.util.List;
import java.util.Map;

public record UpdateBuilderParameter(
        String tableName,
        List<String> columnNames,
        Map<String, String> requestParams,
        Map<String, String> whereClauseParams

) {
}
