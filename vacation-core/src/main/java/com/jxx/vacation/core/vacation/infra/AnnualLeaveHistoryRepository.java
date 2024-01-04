package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.VacationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnualLeaveHistoryRepository extends JpaRepository<VacationHistory, Long> {
}
