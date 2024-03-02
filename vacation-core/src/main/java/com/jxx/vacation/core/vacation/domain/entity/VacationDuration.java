package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
@NoArgsConstructor
public class VacationDuration {

    private static long DATE_ADJUSTMENTS_VALUE = 1l;

    @Column(name = "VACATION_TYPE", nullable = false)
    @Comment(value = "연차 유형")
    @Enumerated(value = EnumType.STRING)
    private VacationType vacationType;

    @Column(name = "START_DATE_TIME", nullable = false)
    @Comment(value = "연차 시작 시간")
    private LocalDateTime startDateTime;
    @Column(name = "END_DATE_TIME", nullable = false)
    @Comment(value = "연차 종료 시간")
    private LocalDateTime endDateTime;

    public VacationDuration(VacationType vacationType, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.vacationType = vacationType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public long calculateDate() {
        return ChronoUnit.DAYS.between(startDateTime, endDateTime) + DATE_ADJUSTMENTS_VALUE;
    }

    public void isInVacationDate(LocalDateTime localDateTime, String clientId) {
        boolean isLocalDateTimeBetween = (localDateTime.isAfter(startDateTime) && localDateTime.isBefore(endDateTime))
                || localDateTime.isEqual(startDateTime) || localDateTime.isEqual(endDateTime);

        if (isLocalDateTimeBetween) {
            throw new VacationClientException(localDateTime + "은 이미 휴가로 신청되어 있는 일자입니다.", clientId);
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

}
