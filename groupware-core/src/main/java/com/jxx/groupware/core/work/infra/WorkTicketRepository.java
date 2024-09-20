package com.jxx.groupware.core.work.infra;

import com.jxx.groupware.core.work.domain.WorkTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WorkTicketRepository extends JpaRepository<WorkTicket, Long> {

    Optional<WorkTicket> findByWorkTicketId(@Param("workTicketId") String workTicketId);

    @Query("select wt from WorkTicket wt join fetch WorkDetail wd " +
            "where wt.workTicketPk =:workTicketPk")
    Optional<WorkTicket> fetchWithWorkDetail(@Param("workTicketPk") Long workTicketPk);
    @Query("select wt from WorkTicket wt join fetch WorkDetail wd " +
            "where wt.workTicketId =:workTicketId")
    Optional<WorkTicket> fetchWithWorkDetail(@Param("workTicketId") String workTicketId);
}
