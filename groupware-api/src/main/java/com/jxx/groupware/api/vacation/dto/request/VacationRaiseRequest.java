package com.jxx.groupware.api.vacation.dto.request;

import com.jxx.groupware.core.messaging.body.vendor.confirm.ConfirmStatus;

public record VacationRaiseRequest(
        ConfirmStatus confirmStatus
) {
}
