package com.jxx.groupware.core.work.domain;

import com.jxx.groupware.core.work.dto.TicketReceiver;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_WORK_TICKET_MASTER",
        indexes = {
                @Index(name = "IDX_WORK_TICKET_ID", columnList = "WORK_TICKET_ID"),
                @Index(name = "IDX_CREATED_TIME_IDX", columnList = "CREATED_TIME"),
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
    @Column(name = "CHARGE_COMPANY_ID")
    @Comment("담당 회사(작업을 수행할) ID")
    private String chargeCompanyId;
    @Comment("담당 부서(작업을 수행할) ID")
    @Column(name = "CHARGE_DEPARTMENT_ID")
    private String chargeDepartmentId;
    @Column(name = "CREATED_TIME")
    @Comment("생성 시간")
    private LocalDateTime createdTime;
    @Column(name = "WORK_STATUS")
    @Comment("작업 진행 상태")
    @Enumerated(value = EnumType.STRING)
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
    public WorkTicket(String requestUUID, String requestTitle, String requestContent, WorkRequester workRequester, String chargeCompanyId,
                      String chargeDepartmentId, LocalDateTime createdTime, WorkStatus workStatus, LocalDateTime modifiedTime) {
        this.workTicketId = requestUUID;
        this.requestTitle = requestTitle;
        this.requestContent = requestContent;
        this.workRequester = workRequester;
        this.chargeCompanyId = chargeCompanyId;
        this.chargeDepartmentId = chargeDepartmentId;
        this.createdTime = createdTime;
        this.workStatus = workStatus;
        this.modifiedTime = modifiedTime;
    }


    /**
     * 연관관계 매핑
     **/
    public void mappingWorkDetail(WorkDetail workDetail) {
        this.workDetail = workDetail;
    }

    /**
     * <pre>
     * WRITE QUERY : JPA dirty checking
     * * 작업 상태 변경 메서드
     * </pre>
     **/
    public void changeWorkStatusTo(WorkStatus workStatus) {
        this.workStatus = workStatus;
        this.modifiedTime = LocalDateTime.now();
    }

    /**
     * <pre>
     * 접수 가능한 티켓인지 검증하는 메서드
     * </pre>
     */

    public boolean isNotReceivable() {
        return !Objects.equals(this.workStatus, WorkStatus.CREATE);
    }

    /**
     * <pre>
     * 분석 단계로 진입할 수 있는 티켓인지 검증하는 메서드
     * </pre>
     */
    public boolean isNotAnalyzable() {
        return !Objects.equals(this.workStatus, WorkStatus.RECEIVE);
    }

    /**
     * 요청자인지 검증
     **/
    public boolean isNotRequester(WorkRequester workRequester) {
        return !this.workRequester.equals(workRequester);
    }

    /**
     * <pre>
     * 작업 티켓과 관련된 요청이 접수자의 요청인지 검증하는 메서드
     * </pre>
     **/
    public boolean isNotReceiverRequest(String receiverId, String chargeCompanyId, String chargeDepartmentId) {
        boolean receiverEqual = Objects.equals(workDetail.getReceiverId(), receiverId);
        boolean chargeCompanyIdEqual = Objects.equals(this.chargeCompanyId, chargeCompanyId);
        boolean chargeDepartmentIdEqual = Objects.equals(this.chargeDepartmentId, chargeDepartmentId);

        return receiverEqual && chargeCompanyIdEqual && chargeDepartmentIdEqual;
    }

    public boolean isNotReceiverRequest(TicketReceiver receiver) {
        boolean receiverEqual = Objects.equals(workDetail.getReceiverId(), receiver.receiverId());
        boolean chargeCompanyIdEqual = Objects.equals(this.chargeCompanyId, receiver.receiverCompanyId());
        boolean chargeDepartmentIdEqual = Objects.equals(this.chargeDepartmentId, receiver.receiverDepartmentId());

        return !(receiverEqual && chargeCompanyIdEqual && chargeDepartmentIdEqual);
    }

    public boolean isNotWorkStatus(WorkStatus workStatus) {
        return !this.workStatus.equals(workStatus);
    }
    public void rejectTicketFromReceiver(String rejectReason) {
        this.workDetail.setRejectReason(rejectReason);
        changeWorkStatusTo(WorkStatus.REJECT_FROM_CHARGE);
    }
}
