package com.jxx.vacation.core.vacation.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.envers.Audited;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

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

    @Embedded
    private VacationDuration vacationDuration;

    @Column(name = "iS_DEDUCTED_FROM_LEAVE", nullable = false)
    @Comment(value = "연차에서 차감되는 휴가 여부")
    private boolean isDeductedFromLeave;

    @Column(name = "VACATION_STATUS", nullable = false)
    @Comment(value = "휴가 신청 상태")
    @Enumerated(value = EnumType.STRING)
    private VacationStatus vacationStatus;


    @Builder
    public Vacation(String requesterId, VacationDuration vacationDuration, boolean isDeductedFromLeave, VacationStatus vacationStatus) {
        this.requesterId = requesterId;
        this.vacationDuration = vacationDuration;
        this.isDeductedFromLeave = isDeductedFromLeave;
        this.vacationStatus = vacationStatus;
    }

    public static Vacation createVacation(String requesterId, VacationDuration vacationDuration) {
        return new Vacation(requesterId, vacationDuration, true, CREATE);
    }

    public boolean validateDeductedLeave() {
        VacationType vacationType = this.vacationDuration.getVacationType();
        this.isDeductedFromLeave = vacationType.isDeductedFromLeave();
        return this.isDeductedFromLeave;
    }

    public void changeVacationStatus(VacationStatus vacationStatus) {
        this.vacationStatus = vacationStatus;
    }

    public boolean isFailVacationStatus() {
        return FAIL.equals(this.vacationStatus);
    }

    public VacationType vacationType() {
        return this.getVacationDuration().getVacationType();
    }

    public boolean isMoreThanDayVacation(){
        VacationType vacationType = vacationType();
        return vacationType.equals(VacationType.MORE_DAY);
    }
}
