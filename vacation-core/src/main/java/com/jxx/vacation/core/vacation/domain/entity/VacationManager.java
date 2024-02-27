package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.message.payload.approval.ConfirmStatus;
import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.MemberLeaveException;
import com.jxx.vacation.core.vacation.domain.service.VacationCalculator;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
public class VacationManager {
    private final float vacationDate;
    private final MemberLeave memberLeave;
    private final Vacation vacation;

    public static VacationManager createVacation(VacationDuration vacationDuration, MemberLeave memberLeave) {
        return new VacationManager(vacationDuration, memberLeave);
    }

    public static VacationManager updateVacation(Vacation vacation, MemberLeave memberLeave) {
        return new VacationManager(vacation, memberLeave);
    }
    private VacationManager(VacationDuration vacationDuration, MemberLeave memberLeave) {
        this.memberLeave = memberLeave;
        this.vacation = create(vacationDuration);
        this.vacationDate = VacationCalculator.getVacationDuration(this.vacation);
    }

    private VacationManager(Vacation vacation, MemberLeave memberLeave) {
        this.vacationDate = VacationCalculator.getVacationDuration(vacation);
        this.memberLeave = memberLeave;
        this.vacation = vacation;
    }

    protected Vacation create(VacationDuration vacationDuration) {
        String requesterId = memberLeave.getMemberId();
        return Vacation
                .createVacation(requesterId, vacationDuration)
                .adjustDeducted();
    }

    public void validateVacation() {
        validateMemberActive(memberLeave, vacation);
        verifyRequestVacation(memberLeave, vacation);
    }

    public void isRaisePossible() {
        VacationStatus vacationStatus = vacation.getVacationStatus();
        if (!(CREATE.equals(vacationStatus) || REJECT.equals(vacationStatus))) {
            throw new IllegalArgumentException("이미 결재가 올라갔거나 종료된 휴가입니다.");
        }
    }

    // 상신
    public void raise(ConfirmStatus confirmStatus) {
        if (ConfirmStatus.RAISE.equals(confirmStatus)) { //결재 문서의 상태가 상신이면
            vacation.changeVacationStatus(REQUEST); // 휴가의 상태도 변경해라.
        }
        else {
            throw new IllegalArgumentException("결재가 상신되지 않았습니다.");
        };
    }

    public void raise(String confirmStatus) {
        raise(ConfirmStatus.valueOf(confirmStatus));
    }

    public float receiveVacationDate() {
        if (Objects.isNull(vacationDate)) {
            throw new IllegalStateException("");
        }
        return vacationDate;
    }

    public Vacation getVacation() {
        return this.vacation;
    }

    public MemberLeave getMemberLeave() {
        return this.memberLeave;
    }

    protected void verifyRequestVacation(MemberLeave memberLeave, Vacation vacation) {
        try {
            memberLeave.checkRemainingLeaveBiggerThan(vacationDate); // 잔여 연차가 차감되는 연차보다 큰지 검증
        } catch (MemberLeaveException e) {
            log.warn("MESSAGE:{}", e.getMessage(), e);
            vacation.changeVacationStatus(FAIL);
        }
    }

    protected void validateMemberActive(MemberLeave memberLeave, Vacation vacation) {
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

    // 휴가 취소 (결재 문서를 취소)
    public void cancel() {
        if (!CANCEL_POSSIBLE_GROUP.contains(vacation.getVacationStatus())) {
            throw new IllegalArgumentException("취소 불가능>.<");
        }

        vacation.changeVacationStatus(CANCELED);
    }

    // 휴가 수정
    public void update(VacationDuration vacationDuration) {
        if (!CREATE.equals(vacation.getVacationStatus())) {
            throw new IllegalArgumentException("수정 불가능>.<");
        }
        vacation.updateVacationDuration(vacationDuration);
    }
}
