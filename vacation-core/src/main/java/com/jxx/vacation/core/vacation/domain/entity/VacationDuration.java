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

    private static long DATE_ADJUSTMENTS_VALUE = 1l;

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

    public void setLastDuration(String lastDuration) {
        this.lastDuration = lastDuration.toUpperCase();
    }



    /**
     * 휴가 종료일이 과거일수록 인덱스를 앞으로 위치시키기 위함
     * 예를 들어 요소 A(종료일 2024-04-15),요소 B(종료일 2024-04-18),요소 C(종료일 2024-04-17)
     * 이면 A,C,B 순으로 정렬된다.
     */
    public int sortByEndDateTime(VacationDuration vacationDuration) {
        return this.endDateTime.isAfter(vacationDuration.getEndDateTime()) ? 1 : -1;
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
