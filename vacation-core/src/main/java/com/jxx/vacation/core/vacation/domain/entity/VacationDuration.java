package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.domain.service.VacationCalculator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "JXX_VACATION_DURATION", indexes = {
        @Index(name = "IDX_START_DATE_TIME", columnList = "START_DATE_TIME"),
        @Index(name = "IDX_END_DATE_TIME", columnList = "END_DATE_TIME")
})
public class VacationDuration {

    private static final long DATE_ADJUSTMENTS_VALUE = 1l;
    private static final String LAST_DURATION_YES_FLAG = "Y";
    private static final int PAST_FLAG = -1;
    private static final int FUTURE_FLAG = 1;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VACATION_DURATION_ID")
    @Comment("휴가 기간 ID")
    private Long id;

    @Column(name = "START_DATE_TIME", nullable = false)
    @Comment(value = "연차 시작 시간")
    private LocalDateTime startDateTime;
    @Column(name = "END_DATE_TIME", nullable = false)
    @Comment(value = "연차 종료 시간")
    private LocalDateTime endDateTime;
    @Column(name = "USE_LEAVE_VALUE", nullable = false)
    @Comment(value = "사용 연차")
    private Float useLeaveValue;

    @Column(name = "LAST_DURATION", nullable = false)
    @Comment(value = "마지막 기간 여부(N:마지막X/Y:마지막O)")
    private String lastDuration;

    @ManyToOne
    @JoinColumn(name = "VACATION_ID", referencedColumnName = "VACATION_ID",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vacation vacation;

    public void mappingVacation(Vacation mappingVacation) {
        vacation = mappingVacation;
        vacation.addVacationDuration(this);
    }

    protected void setLastDurationY() {
        this.lastDuration = LAST_DURATION_YES_FLAG;
    }

    /**
     * 하나의 휴가에는 2개 이상의 기간이 존재할 수 있다.
     * e.g) 에를 들어 주말에 일을 하지 않는 사용자라고 할 때
     * 금요일, 월요일을 각각 기간으로 지정하여 연차로 신청할 수 있다.
     *
     * client 단에서 기간을 과거부터 보낸다는 보장이 없다. 다시 말해, 금, 월 순으로 요청을 보내는게 아니라 월, 금 순으로 보낼 수도 있다.
     * 만약 과거순으로 보낸다는 보장이 없으면 하나의 휴가 중 마지막 기간을 의미하는 LastDuration 필드 값을 결정할 수 없다.
     * 이에 따라 VacationDuration 을 Store 에 저장하기 전에 반드시 호출해야 한다.
     *
     * @param vacationDuration : 비교 대상
     * @return 인자의 endDateTime 보다 미래라면 1 과거라면 -1
     */
    protected int reconciliationVacationDurations(VacationDuration vacationDuration) {
        return endDateTime.isAfter(vacationDuration.getEndDateTime()) ? FUTURE_FLAG : PAST_FLAG;
    }

    public VacationDuration(LocalDateTime startDateTime, LocalDateTime endDateTime, LeaveDeduct leaveDeduct) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.useLeaveValue = VacationCalculator.calculateUseLeaveValue(leaveDeduct, startDateTime, endDateTime);
        this.lastDuration = "N";
    }

    public float calculateDate() {
        long vacationDateCount = ChronoUnit.DAYS.between(startDateTime, endDateTime) + DATE_ADJUSTMENTS_VALUE;
        long notWorkingDateCount = countNotWorkingDate();
        return vacationDateCount - notWorkingDateCount;
    }

    // already
    public void isAlreadyInVacationDate(LocalDateTime localDateTime) {
        boolean isLocalDateTimeBetweenVacationDuration = (localDateTime.isAfter(startDateTime) && localDateTime.isBefore(endDateTime))
                || localDateTime.isEqual(startDateTime) || localDateTime.isEqual(endDateTime);

        if (isLocalDateTimeBetweenVacationDuration) {
            throw new VacationClientException(localDateTime + "은 이미 휴가로 신청되어 있는 일자입니다.");
        }
    }

    public List<LocalDateTime> receiveVacationDateTimes() {
        List<LocalDateTime> dates = new ArrayList<>();
        LocalDateTime current = startDateTime;

        while (!current.isAfter(endDateTime)) {
            dates.add(current);
            current = current.plusDays(1);
        }

        return dates;
    }


    // 휴일, 공휴일...
    public long countNotWorkingDate() {
        List<LocalDateTime> vacationDateTimes = receiveVacationDateTimes();
        return vacationDateTimes.stream()
                .filter(vacationDateTime -> vacationDateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) || vacationDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .count();
    }


}
