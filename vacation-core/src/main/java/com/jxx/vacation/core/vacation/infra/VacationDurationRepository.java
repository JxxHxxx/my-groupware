package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.VacationDuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationDurationRepository extends JpaRepository<VacationDuration, Long> {
}
