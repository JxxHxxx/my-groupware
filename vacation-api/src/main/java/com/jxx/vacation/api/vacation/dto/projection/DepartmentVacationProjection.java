package com.jxx.vacation.api.vacation.dto.projection;

import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class DepartmentVacationProjection {
    private final String name;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final VacationType vacationType;
    private final VacationStatus vacationStatus;
}
