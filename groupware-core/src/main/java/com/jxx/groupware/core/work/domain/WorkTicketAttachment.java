package com.jxx.groupware.core.work.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_WORK_TICKET_ATTACHMENT_MASTER")
public class WorkTicketAttachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workTicketAttachmentPk;

    private String attachmentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_TICKET_PK", referencedColumnName = "WORK_TICKET_PK",  foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private WorkTicket workTicket;
}
