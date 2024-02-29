package com.jxx.vacation.batch.job.leave.processor;

import com.jxx.vacation.batch.job.leave.item.LeaveItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
public class LeaveItemValidateProcessor implements ItemProcessor<LeaveItem, LeaveItem> {
    @Override
    public LeaveItem process(LeaveItem item) throws Exception {
        if (!item.checkMemberOrgActive()) {
            item.updateVacationStatusToError();
            log.info("[PROCESS][member, org inactive]");
            return item;
        }

        if (!ONGOING.isOngoing(item.getVacationStatus())) {
            item.updateVacationStatusToError();
            log.info("[PROCESS][vacation status is not ongoing]");
            return item;
        }
        item.updateVacationStatusToCompleted();
        item.calculateDeductAmount();
        log.info("[PROCESS][{}]", item);
        return item;
    }
}
