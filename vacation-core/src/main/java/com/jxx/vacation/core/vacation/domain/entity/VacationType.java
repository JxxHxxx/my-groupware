package com.jxx.vacation.core.vacation.domain.entity;

import lombok.Getter;

/**
 * 추후 버전에서는 회사마다 휴가 체계가
 */
@Getter
public enum VacationType {
    MORE_DAY("1일 이상의 연차"),
    HALF_MORNING("반차-오전"),
    HALF_AFTERNOON("반차-오전"),
    NOT_DEDUCTED("연차 차감되지 않는 휴가");
    private final String description;

    VacationType(String description) {
        this.description = description;
    }

    boolean isDeductedLeave() {
        return !this.equals(NOT_DEDUCTED);
    }
}
