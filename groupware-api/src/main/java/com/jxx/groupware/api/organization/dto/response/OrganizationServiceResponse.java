package com.jxx.groupware.api.organization.dto.response;

public record OrganizationServiceResponse(
        Long organizationPk,
        Boolean isActive,
        String companyId,
        String companyName,
        String departmentId,
        String departmentName,
        String parentDepartmentId,
        String parentDepartmentName
) {
}
