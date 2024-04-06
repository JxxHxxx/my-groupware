package com.jxx.vacation.batch.job.leave.processor;

import com.jxx.vacation.batch.job.leave.item.LeaveItem;
import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
public class LeaveItemValidateProcessor implements ItemProcessor<LeaveItem, LeaveItem> {

    @Override
    public LeaveItem process(LeaveItem item) throws Exception {
        boolean memberOrgActive = item.checkMemberOrgActive();
        boolean ongoingVacationStatus = isOngoing(item.getVacationStatus());
        String memberId = item.getMemberId();
        String vacationStatus = item.getVacationStatus();

        if (!memberOrgActive) {
            log.info("[PROCESS][FAIL][memberId:{} inactive][member {}, org {}]", memberId, item.isMemberActive(), item.isOrgActive());
        }
        else if (!ongoingVacationStatus) {
            log.info("[PROCESS][FAIL][memberId:{} vacationStatus:{}][vacation must be ongoing]", memberId, vacationStatus);
        }

        if (memberOrgActive && ongoingVacationStatus) {
            item.updateVacationStatusToCompleted();
            item.calculateDeductAmount();
        }

        return memberOrgActive && ongoingVacationStatus ? item : null;
    }
}
