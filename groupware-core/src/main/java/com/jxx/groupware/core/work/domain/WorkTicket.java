package com.jxx.groupware.core.work.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_WORK_TICKET_MASTER",
        indexes = {
        @Index(name = "IDX_WORK_TICKET_ID", columnList = "WORK_TICKET_ID"),
        @Index(name = "IDX_CPN_REQ_ID", columnList = "COMPANY_ID, REQUESTER_ID")})
public class WorkTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_TICKET_PK")
    @Comment("작업 티켓 PK")
    private Long workTicketPk;
    @Column(name = "WORK_TICKET_ID")
    @Comment("작업 티켓 ID ")
    private String workTicketId;
    @Comment("요청 내용")
    private String requestContent;
    @Comment("요청자 소속 회사 ID")
    private String companyId;
    @Comment("요청자 ID")
    private String requesterId;
    @Comment("요청자 이름")
    private String requesterName;
    @Comment("담당 부서(작업을 수행할) ID")
    private String chargeDepartmentId;
    @Comment("생성 시간")
    private LocalDateTime createdTime;
    @Comment("작업 진행 상태")
    private WorkStatus workStatus;
    @Comment("변경 시간")
    private LocalDateTime modifiedTime;
    @OneToOne(fetch = FetchType.LAZY)
    @Comment("작업 내용 간접키")
    private WorkDetail workDetail;
    @OneToMany(mappedBy = "workTicket")
    private List<WorkTicketAttachment> workTicketAttachment = new ArrayList<>();

    @Builder
    public WorkTicket(String requestContent, String companyId, String requesterId, String requesterName,
                      String chargeDepartmentId, LocalDateTime createdTime, WorkStatus workStatus, LocalDateTime modifiedTime,
                      WorkDetail workDetail) {
        this.workTicketId = UUID.randomUUID().toString();
        this.requestContent = requestContent;
        this.companyId = companyId;
        this.requesterId = requesterId;
        this.requesterName = requesterName;
        this.chargeDepartmentId = chargeDepartmentId;
        this.createdTime = createdTime;
        this.workStatus = workStatus;
        this.modifiedTime = modifiedTime;
        this.workDetail = workDetail;
    }
}
