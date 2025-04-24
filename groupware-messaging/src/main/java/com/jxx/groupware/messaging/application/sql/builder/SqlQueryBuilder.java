package com.jxx.groupware.messaging.application.sql.builder;

public interface SqlQueryBuilder {
    String insert(QueryBuilderParameter parameter);
    String update(QueryBuilderParameter parameter);
    String delete(QueryBuilderParameter parameter);
}
