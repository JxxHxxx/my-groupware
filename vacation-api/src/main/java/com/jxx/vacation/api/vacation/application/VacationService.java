package com.jxx.vacation.api.vacation.application;

import com.jxx.vacation.api.vacation.application.function.ConfirmRaiseApiAdapter;
import com.jxx.vacation.api.vacation.dto.request.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.FamilyOccasionPolicyResponse;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.api.vacation.listener.VacationCreatedEvent;
import com.jxx.vacation.api.vacation.query.VacationDynamicMapper;
import com.jxx.vacation.api.vacation.query.VacationSearchCondition;
import com.jxx.vacation.core.common.history.History;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.infra.CompanyVacationTypePolicyRepository;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.VacationHistRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;

import com.jxx.vacation.core.vacation.projection.DepartmentVacationProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.BiFunction;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {

    private final ApplicationEventPublisher eventPublisher;
    private final VacationRepository vacationRepository;
    private final VacationHistRepository vacationHistRepository;
    private final MemberLeaveRepository memberLeaveRepository;
    private final CompanyVacationTypePolicyRepository companyVacationTypePolicyRepository;
    private final VacationDynamicMapper vacationDynamicMapper;

    /**
     * 트랜잭션 커밋/롤백 정책
     * 복구불가능한 비즈니스 로직의 경우 VacationClientException <- 롤백
     * 복구 가능한 경우, 다른 예외를 사용
     */
    @Transactional
    public VacationServiceResponse createVacation(RequestVacationForm vacationForm) {
        String requesterId = vacationForm.requesterId();
        MemberLeave memberLeave = memberLeaveRepository.findMemberLeaveByMemberId(requesterId)
                .orElseThrow(() -> new VacationClientException("requesterId " + requesterId + " not found", requesterId));

        VacationManager vacationManager = VacationManager.createVacation(vacationForm.vacationDuration(), memberLeave);
        vacationManager.validateMemberActive(); // 활성화 된 사용자인지 검증

        List<Vacation> requestingVacations = vacationRepository.findAllByRequesterId(requesterId);
        // 신청일이 이미 휴가로 지정되었거나
        vacationManager.validateVacationDatesAreDuplicated(requestingVacations);
        // TODO 근무일이 아닐 때 휴가를 신청했는지 검증 (테이블 필드 추가 필요)

        Vacation vacation = vacationManager.getVacation();
        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct())) {
            // TODO 매번 연산하는것보다 DB에 박아버리는것도 나쁘지 않을듯.
            vacationManager.validateRemainingLeaveIsBiggerThanConfirmingVacationsAnd(requestingVacations);
        }

        final Vacation savedVacation = vacationRepository.save(vacation);
        vacationHistRepository.save(new VacationHistory(vacation, History.insert(vacation.getRequesterId())));

        if (savedVacation.successRequest()) {
            eventPublisher.publishEvent(new VacationCreatedEvent(memberLeave, vacation, vacationManager.receiveVacationDate(), requesterId));
        }
        return createVacationServiceResponse(savedVacation, memberLeave);
    }

    @Transactional
    public void createVacations(List<RequestVacationForm> requestVacationForms) {
        for (RequestVacationForm requestVacationForm : requestVacationForms) {
            createVacation(requestVacationForm);
        }
    }


    public VacationServiceResponse readOne(String requesterId, Long vacationId) {
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 해당하는 레코드가 존재하지 않습니다."));
        Vacation findVacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 해당하는 레코드가 존재하지 않습니다."));

        return new VacationServiceResponse(
                findVacation.getId(),
                findVacation.getRequesterId(),
                memberLeave.getName(),
                findVacation.getVacationDuration(),
                findVacation.getVacationStatus());
    }

    public List<VacationServiceResponse> readByRequesterId(String requesterId) {
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 해당하는 레코드가 존재하지 않습니다."));
        List<Vacation> oneRequesterVacations = vacationRepository.findAllByRequesterId(requesterId);

        return oneRequesterVacations.stream()
                .map(vacation -> new VacationServiceResponse(
                        vacation.getId(),
                        vacation.getRequesterId(),
                        memberLeave.getName(),
                        vacation.getVacationDuration(),
                        vacation.getVacationStatus()
                )).toList();
    }

    @Transactional
    public VacationServiceResponse raiseVacation(Long vacationId) {
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter = new ConfirmRaiseApiAdapter();
        return raiseVacation(vacationId, apiAdapter);
    }

    @Transactional // 테스팅을 위해
    protected VacationServiceResponse raiseVacation(Long vacationId,
                                                    BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> function) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
        MemberLeave memberLeave = memberLeaveRepository.findMemberWithOrganizationFetch(vacation.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

        VacationManager vacationManager = VacationManager.updateVacation(vacation, memberLeave);
        // validate start
        if (!vacationManager.validateMemberActive()) {
            VacationServiceResponse response = new VacationServiceResponse(vacation.getId(),
                    vacation.getRequesterId(),
                    memberLeave.getName(),
                    vacation.getVacationDuration(),
                    vacation.getVacationStatus());
            return response;
        }

        vacationManager.isRaisePossible();
        // call to another server api
        ConfirmDocumentRaiseResponse response = function.apply(vacation, memberLeave);

        // TODO 상신 요청자를 요청자에서 받고 있지 않음, 세션 or Body 받아서 요청자 누군지 검증해야 함 - 일단 vacation 생성자로 ㄱㄱ
        Vacation riseVacation = vacationManager.raise(response.confirmStatus());
        vacationHistRepository.save(new VacationHistory(riseVacation, History.update(vacation.getRequesterId())));

        return new VacationServiceResponse(vacation.getId(),
                vacation.getRequesterId(),
                memberLeave.getName(),
                vacation.getVacationDuration(),
                vacation.getVacationStatus());
    }

    @Transactional
    public VacationServiceResponse cancelVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow();
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow();

        VacationManager vacationManager = VacationManager.updateVacation(vacation, memberLeave);
        vacationManager.validateMemberActive();
        // TODO 상신 요청자를 요청자에서 받고 있지 않음, 세션 or Body 받아서 요청자 누군지 검증해야 함 - 일단 vacation 생성자로 ㄱㄱ
        Vacation cancelVacation = vacationManager.cancel();
        vacationHistRepository.save(new VacationHistory(cancelVacation, History.update(vacation.getRequesterId())));


        return createVacationServiceResponse(vacation, memberLeave);
    }

    /**
     * 수정 시, 메시지 큐 UPDATE 플래그로 날라가야 함
     */

    @Transactional
    public VacationServiceResponse updateVacation(Long vacationId, RequestVacationForm form) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow();
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow();

        VacationManager vacationManager = VacationManager.updateVacation(vacation, memberLeave);
        vacationManager.validateMemberActive();
        // TODO 상신 요청자를 요청자에서 받고 있지 않음, 세션 or Body 받아서 요청자 누군지 검증해야 함 - 일단 vacation 생성자로 ㄱㄱ
        Vacation updatedVacation = vacationManager.update(form.vacationDuration());
        vacationHistRepository.save(new VacationHistory(updatedVacation, History.update(vacation.getRequesterId())));

        return createVacationServiceResponse(vacation, memberLeave);
    }

    private static VacationServiceResponse createVacationServiceResponse(Vacation vacation, MemberLeave memberLeave) {
        return new VacationServiceResponse(
                vacation.getId(),
                vacation.getRequesterId(),
                memberLeave.getName(),
                vacation.getVacationDuration(),
                vacation.getVacationStatus());
    }

    public List<FamilyOccasionPolicyResponse> findFamilyOccasionPoliciesByCompanyId(String companyId) {
        List<CompanyVacationTypePolicy> policies = companyVacationTypePolicyRepository.findByCompanyId(companyId);

        return policies.stream()
                .map(policy -> new FamilyOccasionPolicyResponse(policy.getCompanyId(), policy.getVacationType(), policy.getVacationDay()))
                .toList();
    }

    public List<DepartmentVacationProjection> searchVacations(VacationSearchCondition condition) {
        return vacationDynamicMapper.search(condition);
    }
    
    
    // 여기 추가 처리해야함
    @Transactional
    public VacationServiceResponse fetchVacationStatus(Long vacationId, VacationStatus vacationStatus) {
        Vacation vacation = vacationRepository.findById(vacationId).orElseThrow();
        if (!(vacationStatus.equals(APPROVED) || vacationStatus.equals(REJECT)))
            throw new VacationClientException("잘못된 요청입니다.");

        if (!REQUEST.equals(vacation.getVacationStatus())) {
            throw new VacationClientException("상신/반려 불가능합니다.");
        }

        vacation.changeVacationStatus(vacationStatus);
        vacationHistRepository.save(new VacationHistory(vacation, History.update(vacation.getRequesterId())));

        return new VacationServiceResponse(
                vacation.getId(),
                vacation.getRequesterId(),
                null,
                vacation.getVacationDuration(),
                vacation.getVacationStatus());
    }
}
