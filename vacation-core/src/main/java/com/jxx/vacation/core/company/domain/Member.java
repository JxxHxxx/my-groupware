package com.jxx.vacation.core.company.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "VACATION_MEMBER_MASTER")
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_PK")
    @Comment(value = "사용자 테이블 PK")
    private Long id;
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

    @Column(name = "REMAINING_ANNUAL_LEAVE_DAYS")
    @Comment(value = "연차 잔여일")
    private Float remainingAnnualLeaveDays;


    // 겸직 사용자를 위해 ManyToOne
    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(referencedColumnName = "COMPANY_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            @JoinColumn(referencedColumnName = "ORGANIZATION_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))})
    private Organization organization;

}
