package com.jxx.vacation.core.vacation.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor
public class VacationDuration {

    private VacationType vacationType;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public VacationDuration(VacationType vacationType, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.vacationType = vacationType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
