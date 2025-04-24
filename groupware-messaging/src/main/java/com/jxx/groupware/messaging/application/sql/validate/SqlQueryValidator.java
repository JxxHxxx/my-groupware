package com.jxx.groupware.messaging.application.sql.validate;

import com.jxx.groupware.messaging.application.sql.builder.QueryBuilderParameter;

/** SqlQueryBuilder 가 QueryBuilderParameter 를 처리할 수 있는지 검증 **/
public interface SqlQueryValidator {
    /**
     * @Return - true : QueryBuilderParameter 가 처리할 수 없음
     * @Return - false - QueryBuilderParameter 가 처리할 수 있음
     * **/
    boolean notValid(QueryBuilderParameter parameter);
}
