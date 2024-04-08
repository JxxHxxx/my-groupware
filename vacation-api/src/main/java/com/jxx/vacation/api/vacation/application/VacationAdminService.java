package com.jxx.vacation.api.vacation.application;

import com.jxx.vacation.api.member.application.UserSession;
import com.jxx.vacation.api.vacation.dto.request.CommonVacationForm;
import com.jxx.vacation.api.vacation.dto.request.CommonVacationServiceForm;
import com.jxx.vacation.api.vacation.dto.request.FamilyOccasionPolicyForm;
import com.jxx.vacation.api.vacation.dto.response.FamilyOccasionPolicyResponse;
import com.jxx.vacation.api.vacation.listener.VacationCreatedEvent;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.infra.FamilyOccasionPolicyRepository;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationAdminService {

    private final FamilyOccasionPolicyRepository familyOccasionPolicyRepository;
    private final MemberLeaveRepository memberLeaveRepository;
    private final VacationRepository vacationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public List<FamilyOccasionPolicyResponse> addFamilyOccasionPolicies(List<FamilyOccasionPolicyForm> forms) {
        List<FamilyOccasionPolicy> policies = forms.stream()
                .map(form -> new FamilyOccasionPolicy(
                        form.companyId(),
                        VacationType.valueOf(form.vacationType()),
                        form.vacationDay()))
                .toList();

        List<FamilyOccasionPolicy> savedPolicies = familyOccasionPolicyRepository.saveAll(policies);
        return savedPolicies.stream()
                .map(policy -> new FamilyOccasionPolicyResponse(policy.getCompanyId(), policy.getVacationType(), policy.getVacationDay()))
                .toList();

    }

    // TODO 몇 명이 반영됐고 몇 일 반영 됐는지
    @Transactional
    public int assignCommonVacation(CommonVacationServiceForm vacationServiceForm) {
        // 검증 전역 관리자 혹은 사내 관리자 검증
        UserSession userSession = vacationServiceForm.userSession();
        CommonVacationForm commonVacationForm = vacationServiceForm.commonVacationForm();
        List<LocalDate> vacationDates = commonVacationForm.vacationDates();

        List<Vacation> vacations = new ArrayList<>();
        for (LocalDate vacationDate : vacationDates) {
            Vacation commonVacation = Vacation.builder()
                    .deducted(true)
                    .vacationStatus(VacationStatus.CREATE)
                    .requesterId(userSession.getMemberId())
                    .companyId(commonVacationForm.companyId())
                    .vacationDuration(new VacationDuration(VacationType.COMMON_VACATION,
                            vacationDate.atStartOfDay(), vacationDate.atTime(23, 59, 59)))
                    .build();

            vacations.add(commonVacation);
        }
        vacationRepository.saveAll(vacations);

        int updateRows = 0;
        // 결재 X, 차감 O 일 경우 바로 차감
        if (!commonVacationForm.mustApproval() && commonVacationForm.deducted()) {
            int leaveDate = vacationDates.size();
            updateRows = memberLeaveRepository.updateRemainingLeave(leaveDate, commonVacationForm.companyId());
        }

        // 결재 O, 차감 X - 이게 조금 까다로울 듯

        // 결재 O, 차감 O

        // 결재가 필요하다면 -- 메시지 보내라...
        if (commonVacationForm.mustApproval()) {
//            savedVacations.stream()
//                    .filter(vacation -> vacation.successRequest())
//                    .peek(// 로그 작업)
//                    .forEach(vacation -> eventPublisher.publishEvent(null));
        }

        return updateRows;
    }
}
