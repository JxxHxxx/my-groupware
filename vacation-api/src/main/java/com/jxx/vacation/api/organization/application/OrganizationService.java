package com.jxx.vacation.api.organization.application;

import com.jxx.vacation.api.organization.dto.response.OrganizationServiceResponse;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.infra.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public List<OrganizationServiceResponse> getCompanyOrganization(String companyId) {
        List<Organization> organizations = organizationRepository.findByCompanyId(companyId);

        return organizations.stream().map(organization -> new OrganizationServiceResponse(
                        organization.getPk(),
                        organization.isActive(),
                        organization.getCompanyId(),
                        organization.getCompanyName(),
                        organization.getDepartmentId(),
                        organization.getDepartmentName(),
                        organization.getParentDepartmentId(),
                        organization.getParentDepartmentName()
                )).toList();
    }

    public List<OrganizationTreeResponse> makeTree(String companyId) {
        List<Organization> organizations = organizationRepository.findByCompanyId(companyId);

        Organization topOrganization = organizations.stream().findFirst()
                .filter(o -> Objects.equals(o.getParentDepartmentId(), "TOP"))
                .orElseThrow();

        List<OrganizationTreeResponse> organizationTree = new ArrayList<>();
        organizationTree.add(new OrganizationTreeResponse(topOrganization.getDepartmentId(),
                topOrganization.getDepartmentName(),
                createSubOrganizationTree(topOrganization, organizations)));

        return organizationTree;
    }

    // 재귀 호출 함수
    private List<OrganizationTreeResponse> createSubOrganizationTree(Organization organization, List<Organization> allOrganizations) {
        List<OrganizationTreeResponse> subDepartments = new ArrayList<>();

        for (Organization org : allOrganizations) {
            if (Objects.equals(org.getParentDepartmentId(), organization.getDepartmentId())) {
                subDepartments.add(new OrganizationTreeResponse(
                        org.getDepartmentId(),
                        org.getDepartmentName(),
                        createSubOrganizationTree(org, allOrganizations)
                ));
            }
        }

        return subDepartments;
    }
}
