package com.jxx.vacation.api.vacation.application;

import com.jxx.vacation.api.vacation.dto.ApprovalServiceResponse;
import com.jxx.vacation.api.vacation.dto.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.response.RequestVacationServiceResponse;
import com.jxx.vacation.core.domain.MessageQ;
import com.jxx.vacation.core.domain.MessageQRepository;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.UnableToApplyVacationException;
import com.jxx.vacation.core.vacation.domain.service.LeaveManager;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final MemberLeaveRepository memberLeaveRepository;
    private final MessageQRepository messageQRepository;

    // create
    @Transactional
    public RequestVacationServiceResponse requestVacation(RequestVacationForm form) {
        log.info("type{} date {}", form.vacationDuration().getVacationType(), form.vacationDuration().getEndDateTime());
        Vacation requestVacation = Vacation.requestVacation(form.requestId(), form.vacationDuration());

        final String requesterId = requestVacation.getRequesterId();
        //사용자 및 조직 활성화 여부 검증
        MemberLeave findMemberLeave = validateActiveMemberLeave(requestVacation, requesterId);
        validateActiveOrganization(requestVacation, findMemberLeave);

        requestVacation.validateDeductedLeave(); // 신청한 휴가가 연차에서 차감해야 하는 휴가인지 체크 및 차감 플래그 설정

         // 차감해야하는 연차 일수 계산
        checkRemainingLeaveIsBiggerThan(requestVacation, findMemberLeave);

        final Vacation savedVacation = vacationRepository.save(requestVacation);// 저장

        if (requestVacation.isFailVacationStatus()) {
            return createRequestVacationServiceResponse(savedVacation, requesterId, findMemberLeave);
        }

        return createRequestVacationServiceResponse(savedVacation, requesterId, findMemberLeave);
    }

    private static RequestVacationServiceResponse createRequestVacationServiceResponse(Vacation requestVacation, String requesterId, MemberLeave findMemberLeave) {
        return new RequestVacationServiceResponse(
                requesterId,
                findMemberLeave.getName(),
                requestVacation.getVacationDuration(),
                requestVacation.getVacationStatus());
    }

    /**
     *
     * @param requestVacation
     * @param findMemberLeave
     * @return : 연차 신청 일
     */
    private static float checkRemainingLeaveIsBiggerThan(Vacation requestVacation, MemberLeave findMemberLeave) {
        float deductionDate = 0F;
        try {
            deductionDate = LeaveManager.calculateDeductionDateBecauseOf(requestVacation);
            findMemberLeave.checkRemainingLeaveIsBiggerThan(deductionDate); // 잔여 연차가 차감되는 연차보다 큰지 검증
        } catch (UnableToApplyVacationException e) {
            log.warn("MESSAGE : {}", e.getMessage(), e);
            requestVacation.changeVacationStatus(FAIL);
        }

        return deductionDate;
    }

    private void validateActiveOrganization(Vacation requestVacation, MemberLeave findMemberLeave) {
        Organization organization = findMemberLeave.getOrganization();

        try {
            organization.checkActive();
        } catch (InactiveException e) {
            log.info("COMPANY ID : {} ORG ID L {} MESSAGE : {}", organization.getCompanyId(), organization.getOrganizationId(),
                    e.getMessage(), e);
            requestVacation.changeVacationStatus(FAIL);
        }
    }

    private MemberLeave validateActiveMemberLeave(Vacation requestVacation, String requesterId) {
        MemberLeave findMemberLeave = memberLeaveRepository.findMemberLeaveByMemberId(requesterId)
                .orElseThrow(() -> new IllegalArgumentException());

        try {
            findMemberLeave.checkActive();
        } catch (InactiveException e) {
            log.info("REQUEST ID : {} MESSAGE : {}", requesterId, e.getMessage(), e);
            requestVacation.changeVacationStatus(FAIL);
        }
        return findMemberLeave;
    }

    // 결재 요청 API

    @Transactional
    public ApprovalServiceResponse approval(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException());

        String memberId = vacation.getRequesterId();
        MemberLeave memberLeave = memberLeaveRepository.findMemberLeaveByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException());

        float deductedVacation = checkRemainingLeaveIsBiggerThan(vacation, memberLeave);

        MessageQ messageQ = MessageQ.builder()
                .requesterId(vacation.getRequesterId())
                .processTime(null)
                .requestVacationDate(deductedVacation)
                .vacationStatus(vacation.getVacationStatus())
                .build();

        MessageQ savedMessageQ = messageQRepository.save(messageQ);

        return new ApprovalServiceResponse(
                savedMessageQ.getPk(),
                savedMessageQ.getMessageStatus(),
                savedMessageQ.getMessageStatus().name());
    }
}
