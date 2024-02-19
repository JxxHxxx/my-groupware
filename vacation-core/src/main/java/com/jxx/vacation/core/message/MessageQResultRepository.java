package com.jxx.vacation.core.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageQResultRepository extends JpaRepository<MessageQResult, Long> {

    List<MessageQResult> findByOriginalMessagePk(Long originalMessagePk);
}
