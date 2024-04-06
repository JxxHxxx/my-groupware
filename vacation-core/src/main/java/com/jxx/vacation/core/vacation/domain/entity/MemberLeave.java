package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.MemberLeaveException;
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
@Table(name = "JXX_MEMBER_LEAVE_MASTER", indexes = @Index(name = "IDX_MEMBER_LEAVE_MEMBER_ID", columnList = "MEMBER_ID", unique = true))
public class MemberLeave {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_PK")
    @Comment(value = "사용자 테이블 PK")
    private Long pk;
    @Column(name = "IS_ACTIVE", nullable = false)
    @Comment(value = "사용자 활성화 여부(0:비활성화 1:활성화)")
    private boolean isActive;
    @Column(name = "MEMBER_ID", unique = true)
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

    // 겸직 사용자를 위해 ManyToOne
    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            @JoinColumn(name = "DEPARTMENT_ID", referencedColumnName = "DEPARTMENT_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))})
    private Organization organization;

    @Builder
    public MemberLeave(Long pk, boolean isActive, String memberId, String name, Integer experienceYears, LocalDate enteredDate, Leave leave, Organization organization) {
        this.pk = pk;
        this.isActive = isActive;
        this.memberId = memberId;
        this.name = name;
        this.experienceYears = experienceYears;
        this.enteredDate = enteredDate;
        this.leave = leave;
        this.organization = organization;
    }

    public void checkActive() throws InactiveException {
        if (!isActive) {
            throw new InactiveException("활성화 되어있지 않은 사용자입니다");
        }
    }

    public boolean checkRemainingLeaveBiggerThan(float deductionLeave) throws MemberLeaveException {
        if (deductionLeave < 0) {
            throw new MemberLeaveException(String.format("잘못된 접근입니다. 신청 연차일 : %f일", deductionLeave));
        };
        Float remainingLeave = leave.getRemainingLeave();
        if (remainingLeave - deductionLeave < 0) {
            throw new MemberLeaveException(String.format("신청 연차 일보다 잔여 연차일이 적습니다. 신청 연차일 : %f일, 잔여 연차일 : %f일",
                    deductionLeave, remainingLeave));
        }
        return true;
    }

    public String receiveCompanyId() {
        return organization.getCompanyId();
    }

    public Float receiveRemainingLeave() {
        return leave.getRemainingLeave();
    }

    public Float receiveTotalLeave() {
        return leave.getTotalLeave();
    }

    public String receiveDepartmentId() {
        return organization.getDepartmentId();
    }

    public void retire() {
        isActive = false;
    }

}
