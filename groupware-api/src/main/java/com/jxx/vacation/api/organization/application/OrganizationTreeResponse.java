package com.jxx.vacation.api.organization.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OrganizationTreeResponse {
    private final String departmentId;
    private final String departmentName;
    private final List<OrganizationTreeResponse> subDepartments;
}
