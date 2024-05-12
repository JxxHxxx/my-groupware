package com.jxx.vacation.api.organization.application;

import com.jxx.vacation.api.organization.dto.response.OrganizationServiceResponse;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.infra.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
