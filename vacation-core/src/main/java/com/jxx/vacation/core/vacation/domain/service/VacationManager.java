package com.jxx.vacation.core.vacation.domain.service;

import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import com.jxx.vacation.core.vacation.domain.exeception.UnableToApplyVacationException;
import lombok.extern.slf4j.Slf4j;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.FAIL;

/**
 * 휴가 검증 flow
 * 1. 사용자, 조직 활성화 여부
 * 1-1 회사에 따라 휴가 정책이 다름
 * 2-1 이미 해당 기간에 휴가가 설정되어 있는지 확인
 * 3. 휴가 종류 : 연차 차감 여부를 확인하기 위함
 * 3-0 차감일 계산 및 상태 저장
 * 3-1 차감 대상 : 보유중인 연차보다 적은 신청일인지 체크
 * 3-2 차감 비대상 : 진행
 */

@Slf4j
public class VacationManager {

    private VacationCalculator vacationCalculator;
    private VacationPolicy vacationPolicy;


    public VacationManager leaveCalculator(VacationCalculator vacationCalculator) {
        this.vacationCalculator = vacationCalculator;
        return this;
    }

    public VacationManager vacationPolicy(VacationPolicy vacationPolicy) {
        this.vacationPolicy = vacationPolicy;
        return this;
    }

    /**
     * @param vacation
     * @return 연차에서 차감되어야 하는 일 수 ex) 0 : 연차 차감 X 휴가, 0.5 : 반차  2 : 이틀...
     */

    public void execute(Vacation vacation, MemberLeave memberLeave) {
        boolean isDeductVacation = vacationPolicy.deductionVacation(vacation);

        float vacationDays = 0F;
        if (isDeductVacation) {
            vacationDays = vacationCalculator.getVacationDays(vacation);
        }

        try {
            memberLeave.checkRemainingLeaveIsBiggerThan(vacationDays);
        } catch (UnableToApplyVacationException e) {
            log.warn("MESSAGE : {}", e.getMessage(), e);
            vacation.changeVacationStatus(FAIL);
        }
    }
}
