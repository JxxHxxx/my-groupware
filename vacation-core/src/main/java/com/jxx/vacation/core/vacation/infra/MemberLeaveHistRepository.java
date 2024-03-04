package com.jxx.vacation.core.vacation.infra;

import com.jxx.vacation.core.vacation.domain.entity.MemberLeaveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLeaveHistRepository extends JpaRepository<MemberLeaveHistory, Long> {
}
