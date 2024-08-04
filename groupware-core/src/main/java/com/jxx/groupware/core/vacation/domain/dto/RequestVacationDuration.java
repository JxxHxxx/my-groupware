package com.jxx.groupware.core.vacation.domain.dto;

import com.jxx.groupware.core.vacation.domain.entity.VacationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.jxx.groupware.core.vacation.domain.entity.VacationType.*;

@Getter
@NoArgsConstructor
public class RequestVacationDuration {
    private Long vacationDurationId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public RequestVacationDuration(Long vacationDurationId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.vacationDurationId = vacationDurationId;
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

