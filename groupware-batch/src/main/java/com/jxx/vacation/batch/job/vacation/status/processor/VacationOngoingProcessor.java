package com.jxx.vacation.batch.job.vacation.status.processor;

import com.jxx.vacation.batch.job.vacation.status.item.VacationItem;
import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;


@Slf4j
public class VacationOngoingProcessor implements ItemProcessor<VacationItem, VacationItem> {
    @Override
    public VacationItem process(VacationItem item) throws Exception {
        boolean approvedVacationStatus = VacationStatus.isApproved(item.getVacationStatus());
        Long vacationId = item.getVacationId();
        String vacationStatus = item.getVacationStatus();

        if (!approvedVacationStatus) {
            // 로그 파일 작업을 위한...
            log.warn("[PROCESS][FILTER][VAC ID:{} VACATION-STATUS:{}][VACATION-STATUS isn't APPROVAL]", vacationId, vacationStatus);
        }
        else {
            item.changeVacationStatus(VacationStatus.ONGOING);
            log.warn("[PROCESS][SUCCESS][VAC ID:{}]", vacationId, vacationStatus);
        }

        return approvedVacationStatus ? item : null;
    }
}
