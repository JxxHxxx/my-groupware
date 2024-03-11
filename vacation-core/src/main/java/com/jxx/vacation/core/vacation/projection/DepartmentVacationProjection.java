package com.jxx.vacation.core.vacation.projection;

import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class DepartmentVacationProjection {

    private final Long vacationId;
    private final String requesterId;
    private final String name; // 사용자 이름
    private final String companyName;
    private final String companyId;
    private final String departmentName;
    private final String departmentId;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final VacationType vacationType;
    private final VacationStatus vacationStatus;
}
