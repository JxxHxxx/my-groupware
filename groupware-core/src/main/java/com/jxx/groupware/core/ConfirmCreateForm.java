package com.jxx.groupware.core;

import java.util.Map;

public record ConfirmCreateForm(
        Long resourceId,
        String companyId,
        String departmentId,
        String departmentName,
        String createSystem,
        String documentType,
        String requesterId,
        String requesterName,
        Map<String, Object> contents
) {
}
