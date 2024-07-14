package com.jxx.groupware.core.vacation.infra;

import com.jxx.groupware.core.vacation.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findOrganizationByCompanyIdAndDepartmentId(String companyId, String DepartmentId);

    List<Organization> findByCompanyId(String companyId);
}
