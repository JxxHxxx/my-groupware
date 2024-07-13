package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.VacationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationHistRepository extends JpaRepository<VacationHistory, Long> {
}
