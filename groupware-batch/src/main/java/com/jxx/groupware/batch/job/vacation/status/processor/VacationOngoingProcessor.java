package com.jxx.groupware.batch.job.vacation.status.processor;

import com.jxx.groupware.batch.job.vacation.status.item.VacationItem;
import com.jxx.groupware.core.vacation.domain.entity.VacationStatus;
import com.jxx.groupware.core.vacation.domain.entity.VacationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Objects;


@Slf4j
public class VacationOngoingProcessor implements ItemProcessor<VacationItem, VacationItem> {
    @Override
    public VacationItem process(VacationItem item) throws Exception {
        boolean approvedVacationStatus = VacationStatus.isApproved(item.getVacationStatus());
        Long vacationId = item.getVacationId();
        String vacationStatus = item.getVacationStatus();
        VacationType vacationType = item.getVacationType();

        // 개인 연차가 아닌 경우, 즉 경조, 특별, 공동 연차가 아닌 경우는 연차 관리를 배치를 통해 하지 않는다.
        if (VacationType.isNotPrivateVacation(vacationType)) {
            log.info("[PROCESS][FILTER][VAC ID:{} VACATION-TYPE:{}][VACATION-TYPE is not in MORE_DAY, HALF_MORNING, HALF_AFTERNOON]", vacationId, vacationType);
            return null;
        }

        if (!approvedVacationStatus) {
            // 로그 파일 작업을 위한...
            log.warn("[PROCESS][FILTER][VAC ID:{} VACATION-STATUS:{}][VACATION-STATUS isn't APPROVAL]", vacationId, vacationStatus);
        }
        else {
            item.changeVacationStatus(VacationStatus.ONGOING);
            log.info("[PROCESS][SUCCESS][VAC ID:{}]", vacationId, vacationStatus);
        }

        return approvedVacationStatus ? item : null;
    }
}
