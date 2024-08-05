package com.jxx.groupware.batch.job.leave.processor;

import com.jxx.groupware.batch.job.leave.item.LeaveItem;
import com.jxx.groupware.core.vacation.domain.entity.LeaveDeduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Objects;

import static com.jxx.groupware.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
public class LeaveItemValidateProcessor implements ItemProcessor<LeaveItem, LeaveItem> {

    // LeaveDeduct.DEDUCT 일 떄만 차감하도록 변경해야함
    @Override
    public LeaveItem process(LeaveItem item) throws Exception {
        boolean memberOrgActive = item.checkMemberOrgActive();
        boolean ongoingVacationStatus = isOngoing(item.getVacationStatus());
        String memberId = item.getMemberId();
        String companyId = item.getCompanyId();
        String vacationStatus = item.getVacationStatus();
        Long vacationId = item.getVacationId();
        String lastDuration = item.getLastDuration();

        if (!memberOrgActive) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} inactive][member {}, org {}]", vacationId, memberId, item.isMemberActive(), item.isOrgActive());
            return null;
        } else if (!ongoingVacationStatus) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} VACATION-STATUS:{}][VACATION-STATUS must be ONGOING]", vacationId, memberId, vacationStatus);
            return null;
        // 차감 연차가 아닌 경우, 처리 (ex. 경조사 연차)
        } else if (!LeaveDeduct.DEDUCT.equals(LeaveDeduct.valueOf(item.getLeaveDeduct()))) {
            Float actualUseLeaveValue = item.getUseLeaveValue();
            if (Objects.equals(item.getUseLeaveValue(), 0F)) {
                log.info("[PROCESS VID:{}][SUCCESS][memberId:{} companyId:{}][Not deduct Leave, useLeaveValue is zero]", vacationId, memberId, companyId);
                return item;
            }
            else {
                log.warn("[PROCESS VID:{}][FILTER][memberId:{} companyId:{}][Not deduct Leave, But useLeaveValue isn't zero, actual value {}]",
                        vacationId, memberId, companyId,actualUseLeaveValue);
                return null;
            }

        } else if (!Objects.equals("Y", lastDuration)) {
            log.info("[PROCESS VID:{}][FILTER][memberId:{} lastDuration:{}][lastDuration is Not Y]", vacationId, memberId, item.getLastDuration());
            return null;
        }

        item.updateVacationStatusToCompleted();
        log.info("[PROCESS VID:{}][SUCCESS][memberId:{} companyId:{}][normal Leave]", vacationId, memberId, companyId);
        return item;
    }
}
