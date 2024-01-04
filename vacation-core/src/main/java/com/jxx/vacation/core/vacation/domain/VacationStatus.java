package com.jxx.vacation.core.vacation.domain;

import lombok.Getter;

@Getter
public enum VacationStatus {

    REQUEST("연차 신청서 작성 완료한 상태"),
    APPROVAL("결재 요청"),
    REJECT("결재 반려"),
    APPROVED("결재 승인"),
    CANCELED("연차 취소");

    private final String description;

    VacationStatus(String description) {
        this.description = description;
    }
}
