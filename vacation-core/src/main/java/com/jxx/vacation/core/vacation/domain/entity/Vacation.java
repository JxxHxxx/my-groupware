package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.vacation.domain.VacationDurationDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "JXX_VACATION_MASTER", indexes = {
        @Index(name = "IDX_REQUESTER_ID", columnList = "REQUESTER_ID")
})
public class Vacation {

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

    @Column(name = "LEAVE_DEDUCT", nullable = false)
    @Comment(value = "연차 차감 여부")
    @Enumerated(EnumType.STRING)
    private LeaveDeduct leaveDeduct;
    @Column(name = "VACATION_STATUS", nullable = false)
    @Comment(value = "휴가 신청 상태")
    @Enumerated(value = EnumType.STRING)
    private VacationStatus vacationStatus;

    @Column(name = "VACATION_TYPE", nullable = false)
    @Comment(value = "연차 유형")
    @Enumerated(value = EnumType.STRING)
    private VacationType vacationType;

    @Column(name = "CREATE_TIME", nullable = false)
    @Comment(value = "레코드 생성 시간")
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "vacation")
    List<VacationDuration> vacationDurations = new ArrayList<>();

    public float useLeaveValueSum() {
        List<Float> vacationDates = vacationDurations.stream()
                .map(vd -> vd.getUseLeaveValue())
                .toList();

        Float useLeaveValue = 0F;
        for (Float vacationDate : vacationDates) {
            useLeaveValue += vacationDate;
        }

        return useLeaveValue;
    }

    @Builder
    public Vacation(String requesterId, String companyId, LeaveDeduct leaveDeduct, VacationType vacationType,
                    VacationStatus vacationStatus) {
        this.requesterId = requesterId;
        this.companyId = companyId;
        this.leaveDeduct = leaveDeduct;
        this.vacationType = vacationType;
        this.vacationStatus = vacationStatus;
        this.createTime = LocalDateTime.now();
    }

    public boolean isDeductVacationType() {
        return vacationType.deductType();
    }
    public static Vacation createDeductVacation(String requesterId, String companyId, VacationType vacationType) {
        return new Vacation(requesterId, companyId, LeaveDeduct.DEDUCT, vacationType, CREATE);
    }

    public static Vacation createNotDeductVacation(String requesterId, String companyId, VacationType vacationType) {
        return new Vacation(requesterId, companyId, LeaveDeduct.NOT_DEDUCT, vacationType, CREATE);
    }

    public void changeVacationStatus(VacationStatus vacationStatus) {
        this.vacationStatus = vacationStatus;
    }

    public boolean successRequest() {
        return CREATE.equals(this.vacationStatus);
    }

    public VacationType vacationType() {
        return this.vacationType;
    }

    public boolean isMoreThanDayVacation(){
        VacationType vacationType = vacationType();
        return vacationType.equals(VacationType.MORE_DAY);
    }

    public List<VacationDurationDto> receiveVacationDurationDto() {
        return vacationDurations.stream()
                .map(vd -> new VacationDurationDto(vd.getId(),vd.getStartDateTime(), vd.getEndDateTime(), vd.getUseLeaveValue()))
                .toList();
    }

    protected void addVacationDuration(VacationDuration vacationDuration) {
        vacationDurations.add(vacationDuration);
    }

    public Float getTotalUseLeaveValue() {
        return vacationDurations.stream().map(vd -> vd.getUseLeaveValue()).reduce((prev, now) -> prev + now).get();
    }

//    protected void updateVacationDuration(List<VacationDuration> vacationDurations) {
//        this.vacationDuration = new VacationDuration(
//                vacationDuration.getVacationType(),
//                vacationDuration.getStartDateTime(),
//                vacationDuration.getEndDateTime());
//    }
}

