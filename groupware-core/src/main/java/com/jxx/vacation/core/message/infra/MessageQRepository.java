package com.jxx.vacation.core.message.infra;

import com.jxx.vacation.core.message.domain.MessageQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageQRepository extends JpaRepository<MessageQ, Long> {

    @Query(value = "SELECT * from JXX_MESSAGE_Q " +
            "WHERE MESSAGE_PROCESS_STATUS = 'SENT' " +
            "LIMIT 1", nativeQuery = true)
    Optional<MessageQ> selectSentOne();

    @Query(value = "SELECT * from JXX_MESSAGE_Q " +
            "WHERE MESSAGE_PROCESS_STATUS = 'RETRY' " +
            "LIMIT 1", nativeQuery = true)
    Optional<MessageQ> selectRetryOne();

    @Query(value = "SELECT * from JXX_MESSAGE_Q " +
            "WHERE MESSAGE_PROCESS_STATUS = 'SENT' " +
            "LIMIT :limit", nativeQuery = true)
    List<MessageQ> findWithLimit(@Param("limit") int limit);
}
