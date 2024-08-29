package com.jxx.groupware.api.organization.application;

import com.jxx.groupware.api.organization.dto.request.CompanyCodeCreateForm;
import com.jxx.groupware.api.organization.dto.request.OrganizationSearchCond;
import com.jxx.groupware.api.organization.dto.response.CompanyCodeResponse;
import com.jxx.groupware.api.organization.dto.response.CompanyDepartmentResponse;
import com.jxx.groupware.api.organization.dto.response.OrganizationServiceResponse;
import com.jxx.groupware.api.organization.query.OrganizationMapper;
import com.jxx.groupware.core.vacation.domain.entity.CompanyCode;
import com.jxx.groupware.core.vacation.domain.entity.Organization;
import com.jxx.groupware.core.vacation.infra.CompanyCodeRepository;
import com.jxx.groupware.core.vacation.infra.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final CompanyCodeRepository companyCodeRepository;

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

    @Transactional
    public CompanyCodeResponse createCompanyCode(CompanyCodeCreateForm form) {
        CompanyCode companyCode = CompanyCode.builder()
                .companyId(form.companyId())
                .companyName(form.companyName())
                .createdTime(LocalDateTime.now())
                .deletedTime(null)
                .used(true)
                .build();

        CompanyCode savedCompanyCode = companyCodeRepository.save(companyCode);

        return new CompanyCodeResponse(
                savedCompanyCode.getCompanyCodePk(),
                companyCode.getCompanyId(),
                companyCode.getCompanyName(),
                companyCode.isUsed(),
                companyCode.getCreatedTime(),
                companyCode.getDeletedTime());
    }

    public List<CompanyCodeResponse> findCompanyCode() {
        List<CompanyCode> companyCodes = companyCodeRepository.findAll();
        return companyCodes.stream().map(companyCode -> new CompanyCodeResponse(
                companyCode.getCompanyCodePk(),
                companyCode.getCompanyId(),
                companyCode.getCompanyName(),
                companyCode.isUsed(),
                companyCode.getCreatedTime(),
                companyCode.getDeletedTime())).toList();
    }

    public List<CompanyDepartmentResponse> findCompanyDepartments(String companyId) {
        List<Organization> companyDepartments = organizationRepository.findCompanyDepartments(companyId);

        return companyDepartments.stream()
                .map(org -> new CompanyDepartmentResponse(
                        org.getDepartmentId(), org.getDepartmentName()))
                .toList();

    }

    public List<OrganizationServiceResponse> searchOrganization(OrganizationSearchCond searchCond) {
        return organizationMapper.search(searchCond);
    }
}
