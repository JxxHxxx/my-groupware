package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.message.body.vendor.confirm.ConfirmStatus;
import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.domain.service.VacationCalculator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
@Getter
public class VacationManager {
    private final MemberLeave memberLeave;
    private final Vacation vacation; // 영속화가 보장되어 있지 않으니 주의

    /**
     * 해당 메서드로 생성 시 Vacation 영속화된 상태가 아니니 주의
     */
    public static VacationManager create(MemberLeave memberLeave, VacationType vacationType, LeaveDeduct leaveDeduct) {
        return new VacationManager(memberLeave, vacationType, leaveDeduct);
    }
    private VacationManager(MemberLeave memberLeave, VacationType vacationType, LeaveDeduct leaveDeduct) {
        this.memberLeave = memberLeave;
        this.vacation = createVacation(vacationType, leaveDeduct);
        validateMemberActive();
    }

    // Vacation Duration
    public Vacation createVacation(VacationType vacationType, LeaveDeduct leaveDeduct) {
        return Vacation.builder()
                .vacationType(vacationType)
                .vacationStatus(CREATE)
                .leaveDeduct(leaveDeduct)
                .requesterId(memberLeave.getMemberId())
                .companyId(memberLeave.receiveCompanyId())
                .build();
    }


    public static VacationManager updateVacation(Vacation vacation, MemberLeave memberLeave) {
        return new VacationManager(vacation, memberLeave);
    }

    // update
    private VacationManager(Vacation vacation, MemberLeave memberLeave) {
        this.memberLeave = memberLeave;
        this.vacation = vacation;
        validateMemberActive();
    }

    protected Vacation create(VacationType vacationType) {
        return decideDeduct(vacationType, memberLeave.getMemberId(), memberLeave.receiveCompanyId());
    }

    private Vacation decideDeduct(VacationType vacationType, String requesterId, String companyId) {
        return VacationType.DEDUCT_TYPE.contains(vacationType.getType()) ?
                Vacation.createDeductVacation(requesterId, companyId, vacationType) :
                Vacation.createNotDeductVacation(requesterId, companyId, vacationType);
    }

    public boolean validateMemberActive() {
        Organization organization = memberLeave.getOrganization();
        try {
            memberLeave.checkActive();
            organization.checkActive();

        } catch (InactiveException e) {
            log.warn("MESSAGE:{}", e.getMessage(), e);
            vacation.changeVacationStatus(FAIL);
            return false;
        }
        return true;
    }

    public void validateRemainingLeaveIsBiggerThanConfirmingVacationsAnd(List<Vacation> alreadyRequestVacations) {
        Float remainingLeave = memberLeave.receiveRemainingLeave();

        // 현재 REQUEST, APPROVED 상태의 휴가 신청일 총 합
        List<Float> vacationDays = alreadyRequestVacations.stream()
                .filter(vacation -> CONFIRMING_GROUP.contains(vacation.getVacationStatus()))
                .map(vacation -> vacation.useLeaveValueSum())
                .toList();

        Float approvingVacationDate = 0F;
        for (Float vacationDay : vacationDays) {
            approvingVacationDate += vacationDay;
        }
        // 신청한 휴가 일 수
        Float requestVacationDate = vacation.useLeaveValueSum();

        String clientId = memberLeave.getMemberId();
        if (remainingLeave - approvingVacationDate - requestVacationDate < 0) {
            throw new VacationClientException("신청 가능한 일 수 " + (remainingLeave - approvingVacationDate) + "일 신청 일 수 " + requestVacationDate + "일", clientId);
        }
    }

//    public void validateVacationDatesAreDuplicated(List<Vacation> requestVacations) {
//        List<VacationDuration> confirmingAndOngoingVacationDurations = requestVacations.stream()
//                .filter(vacation -> CONFIRMING_AND_ONGOING_GROUP.contains(vacation.getVacationStatus()))
//                .map(vacation -> vacation.getVacationDuration())
//                .toList();
//
//        VacationDuration requestVacationDuration = vacation.getVacationDuration();
//        List<LocalDateTime> requestVacationDateTimes = requestVacationDuration.receiveVacationDateTimes();
//
//        for (VacationDuration vacationDuration : confirmingAndOngoingVacationDurations) {
//            for (LocalDateTime requestVacationDateTime : requestVacationDateTimes) {
//                vacationDuration.isAlreadyInVacationDate(requestVacationDateTime);
//            }
//        }
//    }

    public void validateVacationDatesAreDuplicated(List<Vacation> requestVacations) {
        List<Vacation> afterConfirmingVacations = requestVacations.stream()
                .filter(vacation -> CONFIRMING_AND_ONGOING_GROUP.contains(vacation.getVacationStatus()))
                .toList();

        List<VacationDuration> afterConfirmingVacationDurations = new ArrayList<>();
        for (Vacation afterConfirmingVacation : afterConfirmingVacations) {
            List<VacationDuration> vd = afterConfirmingVacation.getVacationDurations();
            afterConfirmingVacationDurations.addAll(vd);
        }

        List<LocalDateTime> requestVacationDateTimes = new ArrayList<>();
        List<VacationDuration> requestVacationDurations = vacation.getVacationDurations();
        for (VacationDuration requestVacationDuration : requestVacationDurations) {
            requestVacationDateTimes.addAll(requestVacationDuration.receiveVacationDateTimes());
        }

        for (VacationDuration vacationDuration : afterConfirmingVacationDurations) {
            for (LocalDateTime requestVacationDateTime : requestVacationDateTimes) {
                vacationDuration.isAlreadyInVacationDate(requestVacationDateTime);
            }
        }
    }

    public void isRaisePossible() {
        VacationStatus vacationStatus = vacation.getVacationStatus();
        if (!(CREATE.equals(vacationStatus))) {
            throw new VacationClientException("이미 결재가 올라갔거나 종료된 휴가입니다.");
        }
    }

    // 상신
    public Vacation raise(ConfirmStatus confirmStatus) {
        if (ConfirmStatus.RAISE.equals(confirmStatus)) { //결재 문서의 상태가 상신이면
            vacation.changeVacationStatus(REQUEST); // 휴가의 상태도 변경해라.
        } else {
            throw new IllegalArgumentException("결재가 상신되지 않았습니다.");
        }
        return vacation;
    }

    public Vacation raise(String confirmStatus) {
        return raise(ConfirmStatus.valueOf(confirmStatus));
    }

    // 휴가 취소 (결재 문서를 취소)
    public Vacation cancel() {
        if (!CANCEL_POSSIBLE_GROUP.contains(vacation.getVacationStatus())) {
            throw new IllegalArgumentException("취소 불가능>.<");
        }
        vacation.changeVacationStatus(CANCELED);
        return vacation;
    }

    // 휴가 수정
//    public Vacation update(VacationDuration vacationDuration) {
//        if (!CREATE.equals(vacation.getVacationStatus())) {
//            throw new IllegalArgumentException("수정 불가능>.<");
//        }
//        vacation.updateVacationDuration(vacationDuration);
//        return vacation;
//    }
}
