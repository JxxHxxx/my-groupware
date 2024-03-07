package com.jxx.vacation.core.vacation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "JXX_VACATION_MASTER", indexes = {
        @Index(name = "IDX_REQUESTER_ID", columnList = "REQUESTER_ID"),
        @Index(name = "IDX_START_DATE_TIME", columnList = "START_DATE_TIME"),
        @Index(name = "IDX_END_DATE_TIME", columnList = "END_DATE_TIME")
})
public class Vacation {

    private final static boolean DEDUCTED_DEFAULT_VALUE = true;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VACATION_ID")
    @Comment(value = "연차 식별자")
    private Long id;

    @Column(name = "REQUESTER_ID", nullable = false)
    @Comment(value = "연차 신청자 ID")
    private String requesterId;

    @Embedded
    private VacationDuration vacationDuration;

    @Column(name = "DEDUCTED", nullable = false)
    @Comment(value = "연차에서 차감 여부")
    private boolean deducted;

    @Column(name = "VACATION_STATUS", nullable = false)
    @Comment(value = "휴가 신청 상태")
    @Enumerated(value = EnumType.STRING)
    private VacationStatus vacationStatus;


    @Builder
    public Vacation(String requesterId, VacationDuration vacationDuration, boolean deducted, VacationStatus vacationStatus) {
        this.requesterId = requesterId;
        this.vacationDuration = vacationDuration;
        this.deducted = deducted;
        this.vacationStatus = vacationStatus;
    }

    public static Vacation createVacation(String requesterId, VacationDuration vacationDuration) {
        return new Vacation(requesterId, vacationDuration, DEDUCTED_DEFAULT_VALUE, CREATE);
    }

    public Vacation adjustDeducted() {
        VacationType vacationType = vacationDuration.getVacationType();
        deducted = vacationType.isDeductedLeave();

        return this;
    }

    public void changeVacationStatus(VacationStatus vacationStatus) {
        this.vacationStatus = vacationStatus;
    }

    public boolean successRequest() {
        return CREATE.equals(this.vacationStatus);
    }

    public VacationType vacationType() {
        return this.getVacationDuration().getVacationType();
    }

    public boolean isMoreThanDayVacation(){
        VacationType vacationType = vacationType();
        return vacationType.equals(VacationType.MORE_DAY);
    }

    protected void updateVacationDuration(VacationDuration vacationDuration) {
        this.vacationDuration = new VacationDuration(
                vacationDuration.getVacationType(),
                vacationDuration.getStartDateTime(),
                vacationDuration.getEndDateTime());
    }

    public VacationType receiveVacationType() {
        return vacationDuration.getVacationType();
    }

    protected void changeDeducted(boolean deducted) {
        this.deducted = deducted;
    }
}

