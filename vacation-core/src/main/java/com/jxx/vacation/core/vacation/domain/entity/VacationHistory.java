package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.common.history.History;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_VACATION_HIST", indexes = @Index(name = "IDX_VACATION_HIST_VACATION_ID", columnList = "VACATION_ID"))

public class VacationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VACATION_HIST_SEQ")
    @Comment(value = "사용자 히스토리 테이블 SEQ")
    private Long seq;

    @Column(name = "VACATION_ID")
    @Comment(value = "휴가 식별자")
    private Long vacationId;

    @Column(name = "REQUESTER_ID", nullable = false)
    @Comment(value = "연차 신청자 ID")
    private String requesterId;

    @Column(name = "COMPANY_ID", nullable = false)
    @Comment(value = "회사 식별자")
    private String companyId;

    @Column(name = "LEAVE_DEDUCT", nullable = false)
    @Comment(value = "연차 차감 여부")
    @Enumerated(EnumType.STRING)
    private LeaveDeduct leaveDeduct;
    @Column(name = "VACATION_STATUS", nullable = false)
    @Comment(value = "휴가 신청 상태")
    @Enumerated(value = EnumType.STRING)
    private VacationStatus vacationStatus;

    @Column(name = "CREATE_TIME", nullable = false)
    @Comment(value = "레코드 생성 시간")
    private LocalDateTime createTime;

    @Embedded
    private History history;

    public VacationHistory(Vacation vacation, History history) {
        this.vacationId = vacation.getId();
        this.requesterId = vacation.getRequesterId();
        this.companyId = vacation.getCompanyId();
        this.leaveDeduct = vacation.getLeaveDeduct();
        this.vacationStatus = vacation.getVacationStatus();
        this.createTime = vacation.getCreateTime();
        this.history = history;
    }
}
