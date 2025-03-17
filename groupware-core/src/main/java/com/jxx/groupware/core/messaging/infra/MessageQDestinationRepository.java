package com.jxx.groupware.core.messaging.infra;

import com.jxx.groupware.core.messaging.domain.destination.MessageQDestination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageQDestinationRepository extends JpaRepository<MessageQDestination, Long> {
}
