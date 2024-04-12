package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.common.Creator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_COMPANY_VACATION_TYPE_POLICY",
        indexes = @Index(name = "IDX_COMPANY_ID_AND_VACATION_TYPE", columnList = "COMPANY_ID, VACATION_TYPE", unique = true))
public class CompanyVacationTypePolicy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPANY_VACATION_TYPE_POLICY_PK")
    @Comment("경조 휴가 정책 PK")
    private Long pk;
    @Column(name = "COMPANY_ID")
    @Comment("회사 코드")
    private String companyId;
    @Column(name = "VACATION_TYPE")
    @Comment("휴가(경조) 유형")
    @Enumerated(EnumType.STRING)
    private VacationType vacationType;
    @Column(name = "VACATION_DAY")
    @Comment("경조 유형에 따른 휴가 일 수")
    private Float vacationDay;
    @Embedded
    private Creator creator;

    @Builder
    public CompanyVacationTypePolicy(String companyId, VacationType vacationType, Float vacationDay, Creator creator) {
        this.companyId = companyId;
        this.vacationType = vacationType;
        this.vacationDay = vacationDay;
        this.creator = creator;
    }
}
