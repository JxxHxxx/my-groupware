package com.jxx.vacation.core.vacation.domain.entity;

import com.jxx.vacation.core.message.body.vendor.confirm.ConfirmStatus;
import com.jxx.vacation.core.vacation.domain.RequestVacationDuration;
import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
@Getter
public class VacationManager {
    private final MemberLeave memberLeave;
    private final Vacation vacation; // 영속화가 보장되어 있지 않으니 주의
    private List<VacationDuration> vacationDurations = new ArrayList<>();

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

    public void createVacationDurations(List<RequestVacationDuration> requestVacationDurations) {
        vacationDurations = requestVacationDurations.stream()
                .map(requestVacationDuration -> {
                    VacationDuration vacationDuration = new VacationDuration(
                            requestVacationDuration.startDateTime(),
                            requestVacationDuration.endDateTime(),
                            vacation.getLeaveDeduct());
                    vacationDuration.mappingVacation(vacation);
                    return vacationDuration;
                })
                .sorted(VacationDuration::reconciliationVacationDurations)
                .toList();

        decideLastDuration();
    }

    private void decideLastDuration() {
        int lastVacationDurationIndex = vacationDurations.size() - 1;
        VacationDuration lastVacationDuration = vacationDurations.get(lastVacationDurationIndex);
        lastVacationDuration.setLastDurationY();
    }

    private Vacation createVacation(VacationType vacationType, LeaveDeduct leaveDeduct) {
        return Vacation.builder()
                .vacationType(vacationType)
                .vacationStatus(CREATE)
                .leaveDeduct(leaveDeduct)
                .requesterId(memberLeave.getMemberId())
                .companyId(memberLeave.receiveCompanyId())
                .build();
    }

    public static VacationDuration createCommonVacationDuration(Vacation commonVacation, LocalDate commonVacationDate) {
        VacationDuration commonVacationDuration = new VacationDuration(commonVacationDate.atStartOfDay(), commonVacationDate.atTime(23, 59, 59), commonVacation.getLeaveDeduct());
        commonVacationDuration.setLastDurationY();
        commonVacationDuration.mappingVacation(commonVacation);

        return commonVacationDuration;
    }

    public static List<LocalDateTime> findAlreadyEnrolledVacationDates(List<Vacation> findVacations, List<LocalDate> requestVacationDates) {
        return findVacations.stream()
                .flatMap(cv -> cv.getVacationDurations().stream())
                .filter(vd -> requestVacationDates.contains(vd.getStartDateTime().toLocalDate()))
                .map(VacationDuration::getStartDateTime)
                .toList();
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

    public void validateVacationDatesAreDuplicated(List<Vacation> createCompletedVacations) {
        List<VacationDuration> requestCompletedVacationDurations = createCompletedVacations.stream()
                .filter(vacation -> CONFIRMING_AND_ONGOING_GROUP.contains(vacation.getVacationStatus()))
                .flatMap(requestCompletedVacation -> requestCompletedVacation.getVacationDurations().stream())
                .toList();

        List<LocalDateTime> requestingVacationDateTimes = new ArrayList<>();
        List<VacationDuration> requestVacationDurations = vacation.getVacationDurations();
        for (VacationDuration requestVacationDuration : requestVacationDurations) {
            requestingVacationDateTimes.addAll(requestVacationDuration.receiveVacationDateTimes());
        }

        for (VacationDuration vacationDuration : requestCompletedVacationDurations) {
            for (LocalDateTime requestVacationDateTime : requestingVacationDateTimes) {
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
