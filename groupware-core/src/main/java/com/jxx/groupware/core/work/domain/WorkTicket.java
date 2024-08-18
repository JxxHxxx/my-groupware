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
                @Index(name = "IDX_CPN_REQ_ID", columnList = "REQUESTER_COMPANY_ID, REQUESTER_ID")})
public class WorkTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_TICKET_PK")
    @Comment("작업 티켓 PK")
    private Long workTicketPk;
    @Column(name = "WORK_TICKET_ID")
    @Comment("작업 티켓 ID")
    private String workTicketId;
    @Column(name = "REQUEST_TITLE")
    @Comment("요청 제목")
    private String requestTitle;
    @Column(name = "REQUEST_CONTENT")
    @Comment("요청 내용")
    private String requestContent;
    @Embedded
    private WorkRequester workRequester;
    @Column(name = "CHARGE_DEPARTMENT_ID")
    @Comment("담당 회사(작업을 수행할) ID")
    private String chargeCompanyId;
    @Comment("담당 부서(작업을 수행할) ID")
    private String chargeDepartmentId;
    @Column(name = "CREATED_TIME")
    @Comment("생성 시간")
    private LocalDateTime createdTime;
    @Column(name = "WORK_STATUS")
    @Comment("작업 진행 상태")
    private WorkStatus workStatus;
    @Column(name = "MODIFIED_TIME")
    @Comment("변경 시간")
    private LocalDateTime modifiedTime;
    @JoinColumn(name = "WORK_DETAIL_PK")
    @OneToOne(fetch = FetchType.LAZY)
    @Comment("작업 내용 간접키")
    private WorkDetail workDetail;
    @OneToMany(mappedBy = "workTicket")
    private List<WorkTicketAttachment> workTicketAttachment = new ArrayList<>();

    @Builder
    public WorkTicket(String requestTitle, String requestContent, WorkRequester workRequester, String chargeCompanyId,
                      String chargeDepartmentId, LocalDateTime createdTime, WorkStatus workStatus, LocalDateTime modifiedTime) {
        this.workTicketId = UUID.randomUUID().toString();
        this.requestTitle = requestTitle;
        this.requestContent = requestContent;
        this.workRequester = workRequester;
        this.chargeCompanyId = chargeCompanyId;
        this.chargeDepartmentId = chargeDepartmentId;
        this.createdTime = createdTime;
        this.workStatus = workStatus;
        this.modifiedTime = modifiedTime;
    }
}
