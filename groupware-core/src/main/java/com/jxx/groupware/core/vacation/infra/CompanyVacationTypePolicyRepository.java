package com.jxx.groupware.core.vacation.infra;

import com.jxx.groupware.core.vacation.domain.entity.CompanyVacationTypePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyVacationTypePolicyRepository extends JpaRepository<CompanyVacationTypePolicy, Long> {

    List<CompanyVacationTypePolicy> findByCompanyId(@Param("companyId") String companyId);

}
