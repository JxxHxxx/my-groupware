package com.jxx.groupware.core.vacation.infra;

import com.jxx.groupware.core.vacation.domain.entity.Organization;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findOrganizationByCompanyIdAndDepartmentId(String companyId, String DepartmentId);

    List<Organization> findByCompanyId(String companyId);

    @Query(value = "select org " +
            "from Organization org " +
            "where org.companyId =:companyId " +
            "and org.isActive = true")
    List<Organization> findCompanyDepartments(@Param("companyId") String companyId);
}
