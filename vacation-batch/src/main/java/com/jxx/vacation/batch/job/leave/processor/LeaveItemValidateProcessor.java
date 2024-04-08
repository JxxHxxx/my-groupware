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
        Long vacationId = item.getVacationId();
        if (!memberOrgActive) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} inactive][member {}, org {}]", vacationId, memberId, item.isMemberActive(), item.isOrgActive());
            return null;
        } else if (!ongoingVacationStatus) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} vacationStatus:{}][vacation must be ongoing]", vacationId, memberId, vacationStatus);
            return null;
        } else if (!item.isDeducted()) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} companyId:{}][is set deducted value false]", vacationId, memberId, item.getCompanyId());
            return null;
        }

        item.updateVacationStatusToCompleted();
        item.calculateDeductAmount();

        return item;
    }
}
