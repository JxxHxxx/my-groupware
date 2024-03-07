package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.message.payload.approval.ConfirmStatus;
import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.domain.service.VacationCalculator;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;
import static com.jxx.vacation.core.vacation.domain.entity.VacationType.*;

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

    public void validateMemberActive() {
        validateMemberActive(memberLeave, vacation);
    }

    public void validateRemainingLeaveIsBiggerThanConfirmingVacationsAnd(List<Vacation> requestVacations) {
        Float remainingLeave = memberLeave.receiveRemainingLeave();
        
        // 현재 REQUEST, APPROVED 상태의 휴가 신청일 총 합
        List<Float> vacationDays = requestVacations.stream()
                .filter(vacation -> CONFIRMING_GROUP.contains(vacation.getVacationStatus()))
                .map(vacation -> vacation.getVacationDuration().calculateDate())
                .toList();

        Float approvingVacationDate = 0F;
        for (Float vacationDay : vacationDays) {
            approvingVacationDate += vacationDay;
        }
        // 신청한 휴가 일 수
        float requestVacationDate = vacation.getVacationDuration().calculateDate();

        String clientId = memberLeave.getMemberId();
        if (remainingLeave - approvingVacationDate - requestVacationDate < 0) {
            throw new VacationClientException("신청 가능한 일 수 " + (remainingLeave - approvingVacationDate) + "일 신청 일 수 " + requestVacationDate + "일", clientId);
        }
    }

    public void validateVacationDatesAreDuplicated(List<Vacation> requestVacations) {
        List<VacationDuration> confirmingAndOngoingVacationDurations = requestVacations.stream()
                .filter(vacation -> CONFIRMING_AND_ONGOING_GROUP.contains(vacation.getVacationStatus()))
                .map(vacation -> vacation.getVacationDuration())
                .toList();

        VacationDuration requestVacationDuration = vacation.getVacationDuration();
        List<LocalDateTime> requestVacationDateTimes = requestVacationDuration.receiveVacationDateTimes();

        for (VacationDuration vacationDuration : confirmingAndOngoingVacationDurations) {
            for (LocalDateTime requestVacationDateTime : requestVacationDateTimes) {
                vacationDuration.isInVacationDate(requestVacationDateTime);
            }
        }
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

    public void decideDeduct() {
        VacationType vacationType = vacation.receiveVacationType();
        if (!DEDUCT_VACATION_TYPE.contains(vacationType)) {
            vacation.changeDeducted(false);
        };
    }
}
