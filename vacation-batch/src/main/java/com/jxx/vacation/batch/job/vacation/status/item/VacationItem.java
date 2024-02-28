package com.jxx.vacation.batch.job.vacation.status.item;


import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class VacationItem {
    private final Long vacationId;
    private final Boolean deducted;
    private final String requesterId;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final VacationType vacationType;
    private String vacationStatus;

    public void changeVacationStatus(VacationStatus vacationStatus) {
        this.vacationStatus = String.valueOf(vacationStatus);
    }
}

