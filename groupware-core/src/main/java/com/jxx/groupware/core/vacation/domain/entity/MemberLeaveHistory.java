package com.jxx.groupware.core.vacation.domain.entity;

import com.jxx.groupware.core.common.history.History;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_MEMBER_LEAVE_HIST", indexes = @Index(name = "IDX_MEMBER_LEAVE_HIST_MEMBER_ID", columnList = "MEMBER_ID"))
public class MemberLeaveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_HIST_SEQ")
    @Comment(value = "사용자 히스토리 테이블 SEQ")
    private Long seq;

    @Column(name = "MEMBER_PK", nullable = false)
    @Comment(value = "사용자 테이블 PK")
    private Long memberPk;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Comment(value = "사용자 활성화 여부")
    private boolean isActive;
    @Column(name = "MEMBER_ID")
    @Comment(value = "사용자 식별자")
    private String memberId;
    @Column(name = "NAME")
    @Comment(value = "사용자 이름")
    private String name;

    @Column(name = "EXPERIENCE_YEARS")
    @Comment(value = "경력 연차")
    private Integer experienceYears;

    @Column(name = "ENTERED_DATE")
    @Comment(value = "입사일자")
    private LocalDate enteredDate;

    @Embedded
    private Leave leave;

    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            @JoinColumn(name = "DEPARTMENT_ID", referencedColumnName = "DEPARTMENT_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))})
    private Organization organization;

    @Embedded
    private History history;

    @Builder
    public MemberLeaveHistory(MemberLeave memberLeave, History history) {
        this.memberPk = memberLeave.getPk();
        this.isActive = memberLeave.isActive();
        this.memberId = memberLeave.getMemberId();
        this.name = memberLeave.getName();
        this.experienceYears = memberLeave.getExperienceYears();
        this.enteredDate = memberLeave.getEnteredDate();
        this.leave = memberLeave.getLeave();
        this.organization = memberLeave.getOrganization();
        this.history = history;
    }
}
