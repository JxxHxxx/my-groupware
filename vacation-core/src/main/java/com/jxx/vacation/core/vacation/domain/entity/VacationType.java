package com.jxx.vacation.core.vacation.domain.entity;

import lombok.Getter;

import java.util.List;

/**
 * 추후 버전에서는 회사마다 휴가 체계가
 */
@Getter
public enum VacationType {
    MORE_DAY("1일 이상의 연차"),
    HALF_MORNING("반차-오전"),
    HALF_AFTERNOON("반차-오전"),
    NOT_DEDUCTED("연차 차감되지 않는 휴가"),

    MARRIAGE_SELF("본인의 결혼"),
    MARRIAGE_CHILD("자녀의 결혼"),
    CHILD_BIRTH_SELF("본인의 출산"),
    CHILD_BIRTH_SPOUSE("배우자의 출산"),

    REVERSE_FORCES("예비군 훈련"),
    DEATH_GRAND_PARENT_SELF("부모의 사망"),
    DEATH_PARENT_SELF("부모의 사망"),
    DEATH_SIBLING_SELF("본인 형제자매의 사망"),
    DEATH_SPOUSE("배우자의 사망"),
    DEATH_CHILD("자녀의 사망");

    private final String description;

    VacationType(String description) {
        this.description = description;
    }

    boolean isDeductedLeave() {
        return !this.equals(NOT_DEDUCTED);
    }

    public static final List<VacationType> HALF_VACATION_TYPE = List.of(HALF_MORNING, HALF_AFTERNOON);
}
