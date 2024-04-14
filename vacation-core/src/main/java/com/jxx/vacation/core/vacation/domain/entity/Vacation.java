package com.jxx.vacation.core.vacation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

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
    @Comment(value = "휴가 식별자")
    private Long id;

    @Column(name = "REQUESTER_ID", nullable = false)
    @Comment(value = "연차 신청자 ID")
    private String requesterId;

    @Column(name = "COMPANY_ID", nullable = false)
    @Comment(value = "회사 식별자")
    private String companyId;

    @Embedded
    private VacationDuration vacationDuration;
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


    @Builder
    public Vacation(String requesterId, String companyId, LeaveDeduct leaveDeduct, VacationDuration vacationDuration, boolean deducted, VacationStatus vacationStatus) {
        this.requesterId = requesterId;
        this.companyId = companyId;
        this.leaveDeduct = leaveDeduct;
        this.vacationDuration = vacationDuration;
        this.vacationStatus = vacationStatus;
        this.createTime = LocalDateTime.now();
    }

    public static Vacation createDeductVacation(String requesterId, String companyId, VacationDuration vacationDuration) {
        return new Vacation(requesterId, companyId, LeaveDeduct.DEDUCT, vacationDuration, DEDUCTED_DEFAULT_VALUE, CREATE);
    }

    public static Vacation createNotDeductVacation(String requesterId, String companyId, VacationDuration vacationDuration) {
        return new Vacation(requesterId, companyId, LeaveDeduct.NOT_DEDUCT, vacationDuration, !DEDUCTED_DEFAULT_VALUE, CREATE);
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
}

