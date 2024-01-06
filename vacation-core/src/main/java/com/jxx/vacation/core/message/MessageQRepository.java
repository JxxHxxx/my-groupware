package com.jxx.vacation.core.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MessageQRepository extends JpaRepository<MessageQ, Long> {

    @Query(value = "SELECT * from JXX_MESSAGE_Q LIMIT 1", nativeQuery = true)
    Optional<MessageQ> selectOnlyOne();
}
