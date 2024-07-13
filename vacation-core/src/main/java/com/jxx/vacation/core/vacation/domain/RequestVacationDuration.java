package com.jxx.vacation.core.vacation.domain;

import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.jxx.vacation.core.vacation.domain.entity.VacationType.*;

@Getter
@NoArgsConstructor
public class RequestVacationDuration {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public RequestVacationDuration(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void reconcliation(VacationType vacationType) {
        if (MORE_DAY.equals(vacationType)) {
            startDateTime = LocalDateTime.of(startDateTime.getYear(), startDateTime.getMonth(), startDateTime.getDayOfMonth(), 0, 0, 0);
            endDateTime = LocalDateTime.of(endDateTime.getYear(), endDateTime.getMonth(), endDateTime.getDayOfMonth(), 23, 59, 59);
        }
    }
}

