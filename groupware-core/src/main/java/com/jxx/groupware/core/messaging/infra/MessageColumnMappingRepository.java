package com.jxx.groupware.core.messaging.infra;

import com.jxx.groupware.core.messaging.domain.destination.rdb.MessageColumnMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageColumnMappingRepository extends JpaRepository<MessageColumnMapping, Long> {

    /** 두개 쿼리 어떻게 나가는지 확인해야됨 **/
    @Query("select mcm from MessageColumnMapping mcm " +
            "where mcm.messageTableMapping.serviceId =:serviceId " +
            "and mcm.columnName =:columnName " +
            "and mcm.messageProcessType =:messageProcessType ")
    Optional<MessageColumnMapping> findByServiceIdAndColumnNameAndMessageProcessType(String serviceId, String columnName, String messageProcessType);

    @Query("select mcm from MessageColumnMapping mcm " +
            "where mcm.messageTableMapping.serviceId =:serviceId")
    List<MessageColumnMapping> findByServiceId(String serviceId);
}
