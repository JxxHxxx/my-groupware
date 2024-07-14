package com.jxx.groupware.core.vacation.domain.entity;

import com.jxx.groupware.core.vacation.domain.dto.VacationDurationDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jxx.groupware.core.vacation.domain.entity.VacationStatus.*;

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
    private List<VacationDuration> vacationDurations = new ArrayList<>();

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
    public void changeVacationStatus(VacationStatus vacationStatus) {
        this.vacationStatus = vacationStatus;
    }

    public boolean successRequest() {
        return CREATE.equals(this.vacationStatus);
    }

    public VacationType vacationType() {
        return this.vacationType;
    }

    public List<VacationDurationDto> receiveVacationDurationDto() {
        return vacationDurations.stream()
                .map(vd -> new VacationDurationDto(vd.getId(),vd.getStartDateTime(), vd.getEndDateTime(), vd.getUseLeaveValue()))
                .toList();
    }

    protected void addVacationDuration(VacationDuration vacationDuration) {
        vacationDurations.add(vacationDuration);
    }

    /** 하나의 휴가에 묶인 휴가 기간의 소진 연차 합계 계산 **/
    public Float getTotalUseLeaveValue() {
        return vacationDurations.stream()
                .map(vd -> vd.getUseLeaveValue())
                .reduce((prev, now) -> prev + now).get();
    }
}

