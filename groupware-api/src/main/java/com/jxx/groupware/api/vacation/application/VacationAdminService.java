package com.jxx.groupware.api.vacation.application;

import com.jxx.groupware.api.member.application.UserSession;
import com.jxx.groupware.api.vacation.application.cache.SimpleCacheContext;
import com.jxx.groupware.api.vacation.dto.request.*;
import com.jxx.groupware.api.vacation.dto.response.CommonVacationServiceResponse;
import com.jxx.groupware.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.groupware.api.vacation.dto.response.VacationTypePolicyResponse;
import com.jxx.groupware.api.vacation.listener.CommonVacationCreateEvent;
import com.jxx.groupware.core.common.Creator;
import com.jxx.groupware.core.vacation.domain.dto.VacationDurationDto;
import com.jxx.groupware.core.vacation.domain.entity.*;
import com.jxx.groupware.core.vacation.domain.exeception.VacationAdminException;
import com.jxx.groupware.core.vacation.domain.exeception.VacationClientException;
import com.jxx.groupware.core.vacation.infra.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jxx.groupware.core.vacation.domain.entity.LeaveDeduct.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationAdminService {

    private final CompanyVacationTypePolicyRepository companyVacationTypePolicyRepository;
    private final MemberLeaveRepository memberLeaveRepository;
    private final VacationRepository vacationRepository;
    private final VacationDurationRepository vacationDurationRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final String COMMON_VACATION_DEPARTMENT_CODE = "ALL";

    @Transactional
    public List<VacationTypePolicyResponse> addCompanyVacationTypePolicies(CompanyVacationTypePolicyRequest request, String adminId) {
        String companyId = request.companyId();
        List<VacationTypePolicyForm> vacationTypePolicyForms = request.vacationTypePolicyForms();
        // 중복 차단을 위한...
        List<VacationType> findVacationTypes = companyVacationTypePolicyRepository.findByCompanyId(companyId)
                .stream()
                .map(CompanyVacationTypePolicy::getVacationType).toList();

        List<VacationType> requestVacationTypes = vacationTypePolicyForms.stream()
                .map(p -> VacationType.valueOf(p.vacationType())).toList();

        Optional<VacationType> oVacationType = requestVacationTypes.stream().filter(findVacationTypes::contains).findFirst();
        if (oVacationType.isPresent()) {
            VacationType vacationType = oVacationType.get();
            throw new VacationAdminException(vacationType.name() + "/"+ vacationType.getDescription() + "은 이미 등록된 TypePolicy 입니다.");
        }

        List<CompanyVacationTypePolicy> companyVacationTypePolicies = vacationTypePolicyForms.stream()
                .map(tpf -> CompanyVacationTypePolicy.builder()
                        .companyId(companyId)
                        .vacationType(VacationType.valueOf(tpf.vacationType()))
                        .vacationDay(tpf.vacationDay())
                        .creator(new Creator(adminId, "ADMIN-API"))
                        .vacationTypeName(tpf.vacationTypeName())
                        .build()).toList();

        List<CompanyVacationTypePolicy> savedPolicies = companyVacationTypePolicyRepository.saveAll(companyVacationTypePolicies);
        return savedPolicies.stream()
                .map(policy -> new VacationTypePolicyResponse(
                        policy.getCompanyId(),
                        policy.getVacationType(),
                        policy.getVacationTypeName(),
                        policy.getVacationDay()))
                .toList();
    }

    // TODO Form 에서 LeaveDeduct Enum 을 받는 식으로 하자.
    @Transactional
    public CommonVacationServiceResponse assignCommonVacation(CommonVacationServiceForm vacationServiceForm) {
        // 검증 전역 관리자 혹은 사내 관리자 검증
        UserSession userSession = vacationServiceForm.userSession();
        CommonVacationForm commonVacationForm = vacationServiceForm.commonVacationForm();
        List<LocalDate> vacationDates = commonVacationForm.vacationDates();

        String companyId = commonVacationForm.companyId();
        // 애플리케이션 캐시에서 긁어옮
        if (SimpleCacheContext.notExistCompany(companyId)) {
            throw new VacationClientException("존재하지 않는 회사 코드 " + companyId +  " 입니다.");
        }

        List<Vacation> findCommonVacation = vacationRepository.findCommonVacation(commonVacationForm.companyId());
        // 중복 신청일 검증
        List<LocalDateTime> alreadyEnrolledVacationDates = VacationManager.findAlreadyEnrolledVacationDates(findCommonVacation, commonVacationForm.vacationDates());

        if (!alreadyEnrolledVacationDates.isEmpty()) {
            log.warn("중복 신청 날짜 {}", alreadyEnrolledVacationDates);
            throw new VacationClientException(alreadyEnrolledVacationDates + " 은 이미 공동 연차로 등록되어 있는 날짜입니다.", userSession.getMemberId());
        }

        final boolean mustApproval = commonVacationForm.mustApproval();
        final boolean deducted = commonVacationForm.deducted();
        LeaveDeduct leaveDeduct = decideLeaveDeduct(mustApproval, deducted);
        // Vacation, VacationDuration INSERT start -- TODO VacationManager 에서 수행하도록 변경
        List<Vacation> vacations = new ArrayList<>();
        List<VacationDuration> vacationDurations = new ArrayList<>();
        for (LocalDate vacationDate : vacationDates) {
            Vacation commonVacation = Vacation.builder()
                    .vacationStatus(VacationStatus.CREATE)
                    .vacationType(VacationType.COMMON_VACATION)
                    .requesterId(userSession.getMemberId()) // check
                    .companyId(commonVacationForm.companyId())
                    .leaveDeduct(leaveDeduct)
                    .build();

            if (!mustApproval) {
                commonVacation.changeVacationStatus(VacationStatus.NON_REQUIRED);
            }

            VacationDuration vacationDuration = VacationManager.createCommonVacationDuration(commonVacation, vacationDate);
            vacationDurations.add(vacationDuration);
            vacations.add(commonVacation);
        }
        List<Vacation> savedVacations = vacationRepository.saveAll(vacations);
        vacationDurationRepository.saveAll(vacationDurations);
        // Vacation, VacationDuration INSERT end

        // 결재 X, 차감 O 일 경우 바로 차감
        int leaveDeductedUserNum = 0; // 연차 차감이 된 사용자의 수

        int totalUseLeaveValue = vacationDates.size();
        if (PRE_DEDUCT.equals(leaveDeduct)) {
            leaveDeductedUserNum = memberLeaveRepository.updateRemainingLeave(totalUseLeaveValue, commonVacationForm.companyId());
        }

        // 결재 승인을 받아야 하는 경우, messageQ 생성
        if (mustApproval) {
            publishVacationCreateEventMessageQ(savedVacations);
        }

        List<VacationServiceResponse> responses = savedVacations.stream()
                .map(savedVacation -> convertToResponse(userSession, savedVacation))
                .toList();
        return new CommonVacationServiceResponse(leaveDeductedUserNum, totalUseLeaveValue, mustApproval, deducted,
                leaveDeduct, responses);
    }

    private LeaveDeduct decideLeaveDeduct(boolean mustApproval, boolean deducted) {
        if (!deducted) {
            return NOT_DEDUCT;
        }
        boolean preDeduct = deducted && !mustApproval;
        return preDeduct ? PRE_DEDUCT : DEDUCT;
    }

    private void publishVacationCreateEventMessageQ(List<Vacation> savedVacations) {
        List<Vacation> succeedVacations = savedVacations.stream()
                .filter(vacation -> vacation.successRequest())
                .toList();

        final float vacationDate = 1f; // 공동 연차는 신청 1개에 하루라는 정책이 존재하기 때문에 해당 값 써도 안전
        for (Vacation succeedVacation : succeedVacations) {
            CommonVacationCreateEvent commonVacationCreateEvent = new CommonVacationCreateEvent(
                    succeedVacation.getRequesterId(),
                    succeedVacation.getCompanyId(),
                    COMMON_VACATION_DEPARTMENT_CODE,
                    vacationDate,
                    succeedVacation.getId()
            );

            eventPublisher.publishEvent(commonVacationCreateEvent);
        }
    }

    private static VacationServiceResponse convertToResponse(UserSession userSession, Vacation savedVacation) {
        List<VacationDuration> savedVacationDurations = savedVacation.getVacationDurations();
        List<VacationDurationDto> vacationDurationDto = savedVacationDurations.stream()
                .map(svd -> new VacationDurationDto(svd.getId(), svd.getStartDateTime(), svd.getEndDateTime(), svd.getUseLeaveValue()))
                .toList();

        return new VacationServiceResponse(
                savedVacation.getId(),
                userSession.getMemberId(),
                userSession.getName(),
                vacationDurationDto,
                savedVacation.getVacationStatus());
    }
}
