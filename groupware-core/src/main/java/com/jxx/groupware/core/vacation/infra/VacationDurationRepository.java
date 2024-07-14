package com.jxx.groupware.core.vacation.infra;

import com.jxx.groupware.core.vacation.domain.entity.VacationDuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationDurationRepository extends JpaRepository<VacationDuration, Long> {
}
