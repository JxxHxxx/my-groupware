package com.jxx.groupware.core.work.domain;

import lombok.Getter;

@Getter
public enum WorkStatus {

    CREATE("생성"),
    ANALYZE("요청 내용 분석"),
    MAKE_PLAN("계획 수립"),
    REQUEST_RAISE("상신 요청"),
    ACCEPT("승인"),
    REJECT_FROM_REQUESTER("요청 부서로부터의 반려"),
    REJECT_FROM_CHARGE("담당 부서로부터의 반려"),
    WORKING("작업중"),
    DONE("작업 완료"),
    DELETE("요청 삭제");

    private String description;

    WorkStatus(String description){

    };
}
