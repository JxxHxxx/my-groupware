package com.jxx.groupware.core.messaging.body.vendor.confirm;

import lombok.Getter;

import java.util.List;


// 결재 애플리케이션 코드
@Getter
public enum ConfirmStatus {
    CREATE("결재 생성"), // 200
    UPDATE("결재 수정"), // 210
    RAISE("결재 상신"),  // 300
    ACCEPT("결재 승인"), // 400
    REJECT("결재 반려"), // 220
    CANCEL("결재 취소"); // 100

    private final String description;

    protected static final List<ConfirmStatus> cancelPossible = List.of(CREATE, UPDATE, REJECT);
    protected static final List<ConfirmStatus> raisePossible = List.of(CREATE, UPDATE, REJECT);

    public static final List<ConfirmStatus> rejectPossibleOfApproval = List.of(RAISE);
    public static final List<ConfirmStatus> acceptPossibleOfApproval = List.of(RAISE);

    ConfirmStatus(String description) {
        this.description = description;
    }
}
