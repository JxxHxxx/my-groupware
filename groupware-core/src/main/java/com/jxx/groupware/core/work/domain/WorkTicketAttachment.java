package com.jxx.groupware.core.work.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_WORK_TICKET_ATTACHMENT_MASTER")
public class WorkTicketAttachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_TICKET_ATTACHMENT_PK")
    @Comment("작업 티켓 첨부 PK")
    private Long workTicketAttachmentPk;
    @Column(name = "ATTACHMENT_URL")
    @Comment("첨부 파일 URL")
    private String attachmentUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_TICKET_PK", referencedColumnName = "WORK_TICKET_PK",  foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private WorkTicket workTicket;

    @Builder
    public WorkTicketAttachment(String attachmentUrl, WorkTicket workTicket) {
        this.attachmentUrl = attachmentUrl;
        this.workTicket = workTicket;
    }
}
