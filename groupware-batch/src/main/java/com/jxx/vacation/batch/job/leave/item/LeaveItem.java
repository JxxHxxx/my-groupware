package com.jxx.vacation.batch.job.leave.item;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
public class LeaveItem {

    private final String memberPk;
    private final LocalDateTime createTime;
    private final boolean memberActive;
    private final Float totalLeave;
    private final Float remainingLeave;
    private final String name;
    private final String memberId;
    private final Integer experienceYears;
    private final LocalDate enteredDate;
    private final Long vacationId;
    private final String leaveDeduct;
    private String vacationStatus;
    private final String vacationType;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final String companyId;
    private final String departmentId;
    private final boolean orgActive;
    private final Float useLeaveValue;
    private final String lastDuration;

    public LeaveItem(String memberPk, LocalDateTime createTime, boolean memberActive, Float totalLeave, Float remainingLeave, String name, String memberId, Integer experienceYears, LocalDate enteredDate,
                     Long vacationId, String leaveDeduct, String vacationStatus, String vacationType, LocalDateTime startDateTime, LocalDateTime endDateTime, String companyId, String departmentId, boolean orgActive, Float useLeaveValue, String lastDuration) {
        this.memberPk = memberPk;
        this.createTime = createTime;
        this.memberActive = memberActive;
        this.totalLeave = totalLeave;
        this.remainingLeave = remainingLeave;
        this.name = name;
        this.memberId = memberId;
        this.experienceYears = experienceYears;
        this.enteredDate = enteredDate;
        this.vacationId = vacationId;
        this.leaveDeduct = leaveDeduct;
        this.vacationStatus = vacationStatus;
        this.vacationType = vacationType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.orgActive = orgActive;
        this.lastDuration = lastDuration;
        this.useLeaveValue = useLeaveValue;
    }

    public boolean checkMemberOrgActive() {
        return memberActive && orgActive ? true : false;
    }

    public void updateVacationStatusToCompleted() {
        this.vacationStatus = "COMPLETED";
    }
}

