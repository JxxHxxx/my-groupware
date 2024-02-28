package com.jxx.vacation.batch.job.leave.config;

import com.jxx.vacation.core.vacation.domain.entity.VacationDuration;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import com.jxx.vacation.core.vacation.domain.service.VacationCalculator;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LeaveItem {

    private String memberId;
    private  boolean memberActive;
    private Float remainingLeave;

    private Long vacationId;
    private boolean deducted;
    private String vacationStatus;
    private String vacationType;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Float deductedAmount;

    private String companyId;
    private String departmentId;
    private boolean orgActive;


    public boolean memberOrgActive() {
        return memberActive && orgActive ? true : false;
    }

    public boolean validateDeductAmount() {
        VacationDuration vacationDuration = new VacationDuration(VacationType.valueOf(vacationType), startDateTime, endDateTime);
        return deductedAmount.equals(VacationCalculator.getVacationDuration(vacationDuration, deducted));
    }

}

