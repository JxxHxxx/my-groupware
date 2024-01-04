package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnualLeaveRepository extends JpaRepository<Vacation, Long> {
}
