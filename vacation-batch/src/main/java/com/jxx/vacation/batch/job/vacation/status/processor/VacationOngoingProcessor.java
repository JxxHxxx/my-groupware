package com.jxx.vacation.batch.job.vacation.status.processor;

import com.jxx.vacation.batch.job.vacation.status.item.VacationItem;
import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;


@Slf4j
public class VacationOngoingProcessor implements ItemProcessor<VacationItem, VacationItem> {
    @Override
    public VacationItem process(VacationItem item) throws Exception {
        if (VacationStatus.isApproval(item.getVacationStatus())) {
            log.info("[PROCESS][FAIL][VAC ID:{} STATUS:{}][vacation-status isn't approval]", item.getVacationId(), item.getVacationStatus());
            item.changeVacationStatus(VacationStatus.ERROR);
        }
        else {
            item.changeVacationStatus(VacationStatus.ONGOING);
        }

        return item;
    }
}
