package com.jxx.vacation.core.vacation.domain.entity;

import lombok.Getter;

import java.util.List;

/**
 * 필드 type 은 휴가 유형을 나타냄 P:개인 F:가족경조 S:그외
 */
@Getter
public enum VacationType {

    MORE_DAY("1일 이상의 연차", "P"),
    HALF_MORNING("반차-오전", "P"),
    HALF_AFTERNOON("반차-오전", "P"),
    NOT_DEDUCTED("연차 차감되지 않는 휴가", "S"),

    MARRIAGE_SELF("본인의 결혼", "F"),
    MARRIAGE_CHILD("자녀의 결혼", "F"),
    CHILD_BIRTH_SELF("본인의 출산", "F"),
    CHILD_BIRTH_SPOUSE("배우자의 출산", "F"),

    REVERSE_FORCES("예비군 훈련", "F"),
    DEATH_GRAND_PARENT_SELF("부모의 사망", "F"),
    DEATH_PARENT_SELF("부모의 사망", "F"),
    DEATH_SIBLING_SELF("본인 형제자매의 사망", "F"),
    DEATH_SPOUSE("배우자의 사망", "F"),
    DEATH_CHILD("자녀의 사망", "F"),

    COMMON_VACATION("공동 연차", "C");

    private final String description;
    private final String type;

    VacationType(String description, String type) {
        this.description = description;
        this.type = type;
    }

    boolean isDeductedLeave() {
        return !this.equals(NOT_DEDUCTED);
    }

    public static final List<VacationType> HALF_VACATION_TYPE = List.of(HALF_MORNING, HALF_AFTERNOON);
    public static final List<VacationType> DEDUCT_VACATION_TYPE = List.of(MORE_DAY, HALF_MORNING, HALF_AFTERNOON);
}
