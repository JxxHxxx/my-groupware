package com.jxx.vacation.core.vacation.domain.entity;

import lombok.Getter;

import java.util.List;

@Getter
public enum VacationStatus {

    CREATE("연차 신청서 작성 완료한 상태"), // 최초
    REQUEST("결재 요청 완료"), // 휴가 서버에서 결정 - REST API
    REJECT("결재 반려"), // 걸재 서버에서 결정
    APPROVED("결재 승인"), // 결재 서버에서 결정
    CANCELED("연차 취소"), // 휴가 서버에서 결정 - REST API
    ONGOING("휴가 사용 중"), // 배치에서 처리
    COMPLETED("연차 소진 완료"), // 배치 동작
    FAIL("신청 실패"),
    ERROR("시스템 상 처리 실패");

    private final String description;

    public static final List<VacationStatus> CANCEL_POSSIBLE_GROUP = List.of(REQUEST, REJECT, APPROVED);
    // 휴가 신청 시, 신청 가능한 일 수 인지 파악해야 할 때, 필요한 그룹
    public static final List<VacationStatus> APPROVING_GROUP = List.of(REQUEST, APPROVED);

    public static final List<VacationStatus> APPROVING_ONGOING_GROUP = List.of(REQUEST, APPROVED, ONGOING);

    VacationStatus(String description) {
        this.description = description;
    }

    public static boolean isOngoing(String vacationStatus) {
        return ONGOING.equals(VacationStatus.valueOf(vacationStatus));
    }

    public static boolean isOngoing(VacationStatus vacationStatus) {
        return ONGOING.equals(vacationStatus);
    }

    public static boolean isApproved(String vacationStatus) {
        return APPROVED.equals(VacationStatus.valueOf(vacationStatus));
    }

    public static boolean isApproved(VacationStatus vacationStatus) {
        return APPROVED.equals(vacationStatus);
    }

}
