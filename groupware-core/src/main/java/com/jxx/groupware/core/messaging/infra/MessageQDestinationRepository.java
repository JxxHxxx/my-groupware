package com.jxx.groupware.core.messaging.infra;

import com.jxx.groupware.core.messaging.domain.destination.MessageQDestination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageQDestinationRepository extends JpaRepository<MessageQDestination, Long> {
    Optional<MessageQDestination> findByDestinationId(String destinationId);
}
