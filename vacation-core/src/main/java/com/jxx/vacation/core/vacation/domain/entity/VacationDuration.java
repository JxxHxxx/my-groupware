package com.jxx.vacation.core.vacation.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

}
