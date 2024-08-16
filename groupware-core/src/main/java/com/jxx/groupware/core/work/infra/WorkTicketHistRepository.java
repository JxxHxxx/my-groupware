package com.jxx.groupware.core.work.infra;

import com.jxx.groupware.core.work.domain.WorkTicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTicketHistRepository extends JpaRepository<WorkTicketHistory, Long> {
}
