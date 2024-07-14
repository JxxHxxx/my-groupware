package com.jxx.groupware.api.vacation.dto.request;

// 결재 서버 문서 상신/취소 API 호출 시 필요한 RequestBody
public record ConfirmRaiseOrCancelRequest(
        String companyId,
        String departmentId,
        String requesterId
) {
}
