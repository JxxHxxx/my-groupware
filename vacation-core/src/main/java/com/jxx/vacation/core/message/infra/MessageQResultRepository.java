package com.jxx.vacation.core.message.infra;

import com.jxx.vacation.core.message.domain.MessageQResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageQResultRepository extends JpaRepository<MessageQResult, Long> {

    List<MessageQResult> findByOriginalMessagePk(Long originalMessagePk);

    @Query("SELECT mqr FROM MessageQResult mqr " +
            "WHERE mqr.originalMessagePk " +
            "   IN (SELECT mqr2.originalMessagePk FROM MessageQResult mqr2 " +
    "               WHERE mqr2.messageProcessStatus = 'FAIL')" +
            "ORDER BY mqr.pk desc " +
            "")
    List <MessageQResult> test();


}
