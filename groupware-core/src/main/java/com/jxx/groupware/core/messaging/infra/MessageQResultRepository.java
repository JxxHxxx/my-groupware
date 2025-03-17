package com.jxx.groupware.core.messaging.infra;

import com.jxx.groupware.core.messaging.domain.queue.MessageQResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageQResultRepository extends JpaRepository<MessageQResult, Long> {

    List<MessageQResult> findByOriginalMessagePk(Long originalMessagePk);

    @Query("select mqr from MessageQResult mqr " +
            "where mqr.processEndTime >= :startDateTime " +
            "and mqr.processEndTime <= :endDateTime " +
            "order by mqr.pk desc ")
    List<MessageQResult> findMessageQResult(@Param("startDateTime") LocalDateTime startDateTime,
                                            @Param("endDateTime") LocalDateTime endDateTime);

}
