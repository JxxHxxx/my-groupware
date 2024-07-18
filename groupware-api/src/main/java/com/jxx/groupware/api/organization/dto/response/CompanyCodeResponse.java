package com.jxx.groupware.api.organization.dto.response;

import java.time.LocalDateTime;

public record CompanyCodeResponse(
        int companyCodePk,
        String companyId,
        String companyName,
        boolean used,
        LocalDateTime createdTime,
        LocalDateTime deletedTime

) {
}
