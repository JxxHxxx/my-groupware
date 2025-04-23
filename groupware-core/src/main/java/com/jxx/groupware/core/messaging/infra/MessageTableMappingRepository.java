package com.jxx.groupware.core.messaging.infra;

import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageTableMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTableMappingRepository extends JpaRepository<MessageTableMapping, Long> {

    Optional<MessageTableMapping> findByDestinationIdAndTableName(String destinationId, String tableName);
}
