package com.jxx.groupware.core.work.infra;

import com.jxx.groupware.core.work.domain.WorkTicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTicketAttachmentRepository extends JpaRepository<WorkTicketAttachment, Long> {
}
