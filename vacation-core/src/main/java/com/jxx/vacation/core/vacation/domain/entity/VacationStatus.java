package com.jxx.vacation.core.vacation.domain.entity;

import lombok.Getter;

@Getter
public enum VacationStatus {

    CREATE("연차 신청서 작성 완료한 상태"), // 최초
    APPROVAL("결재 요청"), // 휴가 서버에서 결정 - REST API
    REJECT("결재 반려"), // 걸재 서버에서 결정
    APPROVED("결재 승인"), // 결재 서버에서 결정
    CANCELED("연차 취소"), // 휴가 서버에서 결정 - REST API
    COMPLETED("연차 소진 완료"), // 배치 동작
    FAIL("신청 실패");

    private final String description;

    VacationStatus(String description) {
        this.description = description;
    }
}
