package com.jxx.groupware.messaging.application.sql.builder;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record InsertBuilderParameter(
        String tableName,
        List<String> columnNames,
        Map<String, String> requestParams
) {

    public Set<String> requestParamKeySet() {
        return requestParams.keySet();
    }
}
