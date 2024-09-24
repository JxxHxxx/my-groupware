package com.jxx.groupware.core.work.domain;

import lombok.Getter;

@Getter
public enum WorkStatus {

    CREATE("생성"),
    RECEIVE("접수"),
    ANALYZE_BEGIN("요청 내용 분석 시작"),
    ANALYZE_COMPLETE("요청 내용 분석 완료"),
    MAKE_PLAN_BEGIN("계획 수립 시작"),
    MAKE_PLAN_COMPLETE("계획 수립 완료"),
    REQUEST_CONFIRM("결재 요청"),
    ACCEPT("작업 티켓 승인"),
    REJECT_FROM_REQUESTER("요청 부서로부터의 반려"),
    REJECT_FROM_CHARGE("담당 부서로부터의 반려"),
    WORKING("작업중"),
    DONE("작업 완료"),
    DELETE("요청 삭제");

    private String description;

    WorkStatus(String description){

    };
}
