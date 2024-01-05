package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findOrganizationByCompanyIdAndOrganizationId(String companyId, String OrganizationId);
}
