package com.jxx.groupware.core.vacation.infra;

import com.jxx.groupware.core.vacation.domain.entity.CompanyCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyCodeRepository extends JpaRepository<CompanyCode, Integer> {
}
