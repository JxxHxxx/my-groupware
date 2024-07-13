package com.jxx.vacation.batch.job.leave.processor;

import com.jxx.vacation.batch.job.leave.item.LeaveItem;
import com.jxx.vacation.core.vacation.domain.entity.LeaveDeduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
public class LeaveItemValidateProcessor implements ItemProcessor<LeaveItem, LeaveItem> {

    // LeaveDeduct.DEDUCT 일 떄만 차감하도록 변경해야함
    @Override
    public LeaveItem process(LeaveItem item) throws Exception {
        boolean memberOrgActive = item.checkMemberOrgActive();
        boolean ongoingVacationStatus = isOngoing(item.getVacationStatus());
        String memberId = item.getMemberId();
        String vacationStatus = item.getVacationStatus();
        Long vacationId = item.getVacationId();
        String lastDuration = item.getLastDuration();

        if (!memberOrgActive) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} inactive][member {}, org {}]", vacationId, memberId, item.isMemberActive(), item.isOrgActive());
            return null;
        } else if (!ongoingVacationStatus) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} VACATION-STATUS:{}][VACATION-STATUS must be ONGOING]", vacationId, memberId, vacationStatus);
            return null;
        } else if (!LeaveDeduct.DEDUCT.equals(LeaveDeduct.valueOf(item.getLeaveDeduct()))) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} companyId:{}][is set deducted value false]", vacationId, memberId, item.getCompanyId());
            return null;
        } else if (!"Y".equals(lastDuration)) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} lastDuration:{}][lastDuration is Not Y]", vacationId, memberId, item.getLastDuration());
            return null;
        }

        item.updateVacationStatusToCompleted();
        log.info("[PROCESS VID:{}][SUCCESS][memberId:{} inactive][member {}, org {}]", vacationId, memberId, item.isMemberActive(), item.isOrgActive());
        return item;
    }
}
