package com.jxx.groupware.core.vacation.infra;

import com.jxx.groupware.core.vacation.domain.entity.VacationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationHistRepository extends JpaRepository<VacationHistory, Long> {
}
