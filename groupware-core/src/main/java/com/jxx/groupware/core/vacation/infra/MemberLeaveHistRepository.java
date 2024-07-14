package com.jxx.groupware.core.vacation.infra;

import com.jxx.groupware.core.vacation.domain.entity.MemberLeaveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLeaveHistRepository extends JpaRepository<MemberLeaveHistory, Long> {
}
