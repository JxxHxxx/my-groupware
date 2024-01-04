package com.jxx.vacation.core.vacation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.envers.Audited;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_VACATION_MASTER")
@Audited
public class Vacation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VACATION_ID")
    @Comment(value = "연차 식별자")
    private Long id;

    @Column(name = "REQUESTER_ID", nullable = false)
    @Comment(value = "연차 신청자 ID")
    private String requesterId;

    // 기간
    @Embedded
    private VacationDuration vacationDuration;

    @Comment(value = "연차에서 차감되는 휴가 여부")
    private boolean isDeductedFromLeave;

    @Column(name = "VACATION_STATUS", nullable = false)
    @Comment(value = "연차 상태")
    private VacationStatus vacationStatus;


}
