package com.jxx.groupware.api.work.dto.request;

import com.jxx.groupware.core.work.domain.WorkRequester;

public record WorkTicketDeleteRequest(
        WorkRequester workRequester
) {
}
