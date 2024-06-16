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
     * 휴가 생성 시 사용
     * 해당 메서드로 생성 시 Vacation 영속화된 상태가 아니니 주의
     */
    public static VacationManager createVacation(MemberLeave memberLeave, VacationType vacationType, LeaveDeduct leaveDeduct) {
        return new VacationManager(memberLeave, vacationType, leaveDeduct);
    }

    /**
     * 휴가 수정 시 사용
     */
    public static VacationManager updateVacation(MemberLeave memberLeave, Vacation vacation) {
        return new VacationManager(memberLeave, vacation);
    }

    private VacationManager(MemberLeave memberLeave, VacationType vacationType, LeaveDeduct leaveDeduct) {
        this.memberLeave = memberLeave;
        this.vacation = createVacation(vacationType, leaveDeduct);
        validateMemberActive();
    }

    private VacationManager(MemberLeave memberLeave, Vacation vacation) {
        this.memberLeave = memberLeave;
        this.vacation = vacation;
        this.vacationDurations = vacation.getVacationDurations();
        validateMemberActive();
    }

    public void validateUpdatePossible() {
        if (!CREATE.equals(vacation.getVacationStatus())) {
            log.info("휴가 수정 요청 불가 vacationId:{} vacationStatus:{}", vacation.getId(), vacation.getVacationStatus());
            throw new VacationClientException("수정할 수 없는 결재 문서입니다. vacationStatus:" + vacation.getVacationStatus());
        }
    }

    public void createVacationDurations(VacationType vacationType, List<RequestVacationDuration> requestVacationDurations) {
        this.vacationDurations = requestVacationDurations.stream()
                .map(requestVacationDuration -> {
                    VacationDuration vacationDuration = new VacationDuration(
                            requestVacationDuration.getStartDateTime(),
                            requestVacationDuration.getEndDateTime(),
                            vacation.getLeaveDeduct(),
                            vacationType);
                    vacationDuration.mappingVacation(vacation);
                    return vacationDuration;
                })
                .sorted(VacationDuration::reconciliationVacationDurations)
                .toList();

        decideLastDuration();
    }

    public void decideLastDuration() {
        int lastVacationDurationIndex = vacationDurations.size() - 1;
        VacationDuration lastVacationDuration = vacationDurations.get(lastVacationDurationIndex);
        lastVacationDuration.setLastDurationY();
    }

    /** 업데이트 할 때 사용 **/
    public void updateLastDuration() {
        Long lastVacationDurationId = Long.MIN_VALUE;
        LocalDateTime lastVacationDurationEndDateTime = LocalDateTime.MIN;
        for (VacationDuration vacationDuration : vacationDurations) {
            vacationDuration.setLastDurationN();
            // 마지막 휴가 기간을 측정하기 위한 작업
            if (vacationDuration.getEndDateTime().isAfter(lastVacationDurationEndDateTime)) {
                lastVacationDurationEndDateTime = vacationDuration.getEndDateTime();
                lastVacationDurationId = vacationDuration.getId();
            }
        }

        Long finalLastVacationDurationId = lastVacationDurationId;

        vacationDurations.stream()
                .filter(vd -> vd.identifyVacationDuration(finalLastVacationDurationId))
                .findFirst()
                .orElseThrow(() -> new VacationClientException("휴가 기간 수정 중에 오류가 발생했습니다."))
                .setLastDurationY();
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
        VacationDuration commonVacationDuration = new VacationDuration(
                commonVacationDate.atStartOfDay(),
                commonVacationDate.atTime(23, 59, 59), commonVacation.getLeaveDeduct(),
                commonVacation.getVacationType());

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

    public void validateVacationDatesAreDuplicated(List<Vacation> createdVacations) {
        // 상신 - 승인 - 휴가 진행 상태의 휴가 기간
        List<VacationDuration> vacationDurationsOfRaiseOrApproveOrOngoingStatus = createdVacations.stream()
                .filter(vacation -> RAISE_APPROVE_ONGOING_VS.contains(vacation.getVacationStatus()))
                // 하나의 휴가에 여러개의 휴가 기간이 포함될 수 있어 flatMap
                .flatMap(filteredVacation -> filteredVacation.getVacationDurations().stream())
                .toList();

        // 현재 신청/수정하려는 휴가 기간의 날짜 저장해두는 리스트
        List<LocalDateTime> requestingVacationDateTimes = vacation.getVacationDurations().stream()
                .flatMap(requestVacationDuration -> requestVacationDuration.receiveVacationDateTimes().stream())
                .toList();

        // 이미 상신, 승인, 진행중인 휴가의 기간중에 현재 신청/수정하려는 휴가의 기간이 이미 지정되어 있는지 확인
        for (VacationDuration vacationDuration : vacationDurationsOfRaiseOrApproveOrOngoingStatus) {
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
            throw new IllegalArgumentException("취소 불가능한 상태입니다.");
        }
        vacation.changeVacationStatus(CANCELED);
        return vacation;
    }
}
