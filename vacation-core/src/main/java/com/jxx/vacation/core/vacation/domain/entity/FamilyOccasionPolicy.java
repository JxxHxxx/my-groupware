package com.jxx.vacation.core.vacation.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_FAMILY_OCCASION_POLICY")
public class FamilyOccasionPolicy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAMILY_OCCASION_POLICY_PK")
    @Comment("경조 휴가 정책 PK")
    private Long pk;
    @Column(name = "COMPANY_ID")
    @Comment("회사 코드")
    private String companyId;
    @Column(name = "COMPANY_ID")
    @Comment("경조 유형")
    @Enumerated(EnumType.STRING)
    private VacationType vacationType;
    @Column(name = "VACATION_DAY")
    @Comment("경조 유형에 따른 휴가 일 수")
    private Float VacationDay;

    @Builder
    public FamilyOccasionPolicy(String companyId, VacationType vacationType, Float vacationDay) {
        this.companyId = companyId;
        this.vacationType = vacationType;
        VacationDay = vacationDay;
    }
}
