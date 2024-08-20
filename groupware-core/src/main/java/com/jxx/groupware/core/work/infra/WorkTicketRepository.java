package com.jxx.groupware.core.work.infra;

import com.jxx.groupware.core.work.domain.WorkTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkTicketRepository extends JpaRepository<WorkTicket, Long> {

    Optional<WorkTicket> findByWorkTicketId(@Param("workTicketId") String workTicketId);
}
