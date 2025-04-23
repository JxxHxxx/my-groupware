package com.jxx.groupware.core.messaging.infra;

import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageColumnMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageColumnMappingRepository extends JpaRepository<MessageColumnMapping, Long> {
}
