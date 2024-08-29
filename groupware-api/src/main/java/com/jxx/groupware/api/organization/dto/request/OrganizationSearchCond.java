package com.jxx.groupware.api.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
public record OrganizationSearchCond(
        @NotBlank
        String companyId,
        String departmentName
) {
}
