package com.jxx.vacation.core.message.infra;

import com.jxx.vacation.core.message.domain.MessageQResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageQResultRepository extends JpaRepository<MessageQResult, Long> {

    List<MessageQResult> findByOriginalMessagePk(Long originalMessagePk);

    @Query("select mqr from MessageQResult mqr " +
            "where mqr.processEndTime >= :startDateTime " +
            "and mqr.processEndTime <= :endDateTime")
    List<MessageQResult> findMessageQResult(@Param("startDateTime") LocalDateTime startDateTime,
                                            @Param("endDateTime") LocalDateTime endDateTime);

}
