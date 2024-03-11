package com.jxx.vacation.core.vacation.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_APPROVAL_LINE_MASTER")
public class ApprovalLine {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPROVAL_LINE_PK")
    @Comment("결재 라인 식별자")
    private Long pk;
    @ManyToOne
    @JoinColumn(name = "VACATION_ID", referencedColumnName = "VACATION_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vacation vacation;
    @Comment("결재 순서(1부터 시작)")
    @Column(name = "APPROVAL_ORDER")
    private Integer approvalOrder;
    @Comment("결재자 ID")
    @Column(name = "APPROVAL_ID")
    private String approvalId;
    @Comment("결재자 이름")
    @Column(name = "APPROVAL_NAME")
    private String approvalName;
    @Enumerated(EnumType.STRING)
    @Comment("결재 문서에 대한 결재자의 승인 여부")
    @Column(name = "APPROVAL_STATUS")
    private ApprovalStatus approvalStatus;
    @Comment("결재 문서에 대해 승인/반려를 결정한 시간")
    @Column(name = "DECIDED_TIME")
    private LocalDateTime decidedTime;
}
