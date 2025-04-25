package com.jxx.groupware.messaging.application.sql.builder;

public interface SqlQueryBuilder {
    String insert(InsertBuilderParameter parameter);
    String update(UpdateBuilderParameter parameter);
    String delete(DeleteBuilderParameter parameter);
}
