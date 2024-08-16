package com.jxx.groupware.core.work.infra;

import com.jxx.groupware.core.work.domain.WorkTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTicketRepository extends JpaRepository<WorkTicket, Long> {
}
