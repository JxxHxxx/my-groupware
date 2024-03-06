package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.FamilyOccasionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FamilyOccasionPolicyRepository extends JpaRepository<FamilyOccasionPolicy, Long> {

    List<FamilyOccasionPolicy> findByCompanyId(@Param("companyId") String companyId);
}
