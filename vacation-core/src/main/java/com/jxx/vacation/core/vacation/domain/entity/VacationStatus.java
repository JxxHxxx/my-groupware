package com.jxx.vacation.core.vacation.domain.entity;

import lombok.Getter;

@Getter
public enum VacationStatus {

    REQUEST("연차 신청서 작성 완료한 상태"),
    APPROVAL("결재 요청"),
    REJECT("결재 반려"),
    APPROVED("결재 승인"),
    CANCELED("연차 취소"),
    COMPLETED("연차 소진 완료"),
    FAIL("신청 실패");

    private final String description;

    VacationStatus(String description) {
        this.description = description;
    }
}
