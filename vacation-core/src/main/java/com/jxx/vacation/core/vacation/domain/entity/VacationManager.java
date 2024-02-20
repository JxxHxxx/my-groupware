package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.message.payload.approval.ConfirmStatus;
import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.UnableToApplyVacationException;
import com.jxx.vacation.core.vacation.domain.service.VacationCalculator;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
public class VacationManager {
    private float vacationDate;

    public Vacation create(MemberLeave memberLeave, VacationDuration vacationDuration) {
        Vacation vacation = Vacation
                .createVacation(memberLeave.getMemberId(), vacationDuration)
                .adjustDeducted();

        validateRequester(memberLeave, vacation);

        return vacation;
    }
    public VacationManager validateRequester(MemberLeave memberLeave, Vacation vacation) {
        validateMemberActive(memberLeave, vacation);
        verifyRequestVacation(memberLeave, vacation);
        return this;
    }

    private void verifyRequestVacation(MemberLeave memberLeave, Vacation vacation) {
        try {
            vacationDate = VacationCalculator.getVacationDuration(vacation);
            memberLeave.checkRemainingLeaveBiggerThan(vacationDate); // 잔여 연차가 차감되는 연차보다 큰지 검증
        } catch (UnableToApplyVacationException e) {
            log.warn("MESSAGE:{}", e.getMessage(), e);
            vacation.changeVacationStatus(FAIL);
        }
    }

    private void validateMemberActive(MemberLeave memberLeave, Vacation vacation) {
        try {
            Organization organization = memberLeave.getOrganization();
            memberLeave.checkActive();
            organization.checkActive();
        }
        catch (InactiveException e) {
            log.warn("MESSAGE:{}", e.getMessage(), e);
            vacation.changeVacationStatus(FAIL);
        }
    }

    public void verifyRaisePossible(Vacation vacation) {
        VacationStatus vacationStatus = vacation.getVacationStatus();
        if (!(CREATE.equals(vacationStatus) || REJECT.equals(vacationStatus))) {
            throw new IllegalArgumentException("이미 결재가 올라갔거나 종료된 휴가입니다.");
        }
    }

    public void processRaise(Vacation vacation, ConfirmStatus confirmStatus) {
        if (ConfirmStatus.RAISE.equals(confirmStatus)) {
            vacation.changeVacationStatus(REQUEST);
        }
        else {
            throw new IllegalArgumentException("결재가 상신되지 않았습니다.");
        };
    }

    public void processRaise(Vacation vacation, String confirmStatus) {
        processRaise(vacation, ConfirmStatus.valueOf(confirmStatus));
    }

    public float receiveVacationDate() {
        if (Objects.isNull(vacationDate)) {
            throw new IllegalStateException("");
        }
        return vacationDate;
    }
}
