package com.jxx.vacation.api.vacation.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.vacation.api.common.web.SimpleRestClient;
import com.jxx.vacation.api.vacation.dto.request.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.request.ConfirmRaiseRequest;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.FamilyOccasionPolicyResponse;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import com.jxx.vacation.api.vacation.listener.VacationCreatedEvent;
import com.jxx.vacation.api.vacation.query.VacationDynamicMapper;
import com.jxx.vacation.api.vacation.query.VacationSearchCondition;
import com.jxx.vacation.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.infra.FamilyOccasionPolicyRepository;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import com.jxx.vacation.core.vacation.projection.DepartmentVacationProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {

    private static final String CONFIRM_SERVER_HOST = "http://localhost:8000";

    private final ApplicationEventPublisher eventPublisher;
    private final VacationRepository vacationRepository;
    private final MemberLeaveRepository memberLeaveRepository;
    private final FamilyOccasionPolicyRepository familyOccasionPolicyRepository;
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

        vacationManager.decideDeduct();
        Vacation vacation = vacationManager.getVacation();
        if (vacation.isDeducted()) {
            // TODO 매번 연산하는것보다 DB에 박아버리는것도 나쁘지 않을듯.
            vacationManager.validateRemainingLeaveIsBiggerThanConfirmingVacationsAnd(requestingVacations);
        }

        final Vacation savedVacation = vacationRepository.save(vacation);
        if (savedVacation.successRequest()) {
            eventPublisher.publishEvent(new VacationCreatedEvent(memberLeave, vacation, vacationManager.receiveVacationDate(), requesterId));
        }
        return createVacationServiceResponse(savedVacation, memberLeave);
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
    public void createVacations(List<RequestVacationForm> requestVacationForms) {
        for (RequestVacationForm requestVacationForm : requestVacationForms) {
            createVacation(requestVacationForm);
        }
    }

    @Transactional
    public ConfirmDocumentRaiseResponse raiseVacation(Long vacationId) throws JsonProcessingException {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

        VacationManager vacationManager = VacationManager.updateVacation(vacation, memberLeave);
        vacationManager.validateMemberActive();

        vacationManager.isRaisePossible(); // 상신이 가능한 상태이면.
        ConfirmDocumentRaiseResponse response = requestVacationRaiseApi(vacation, memberLeave); // 외부 통신
        vacationManager.raise(response.confirmStatus());

        return response;

    }

    private ConfirmDocumentRaiseResponse requestVacationRaiseApi(Vacation vacation, MemberLeave memberLeave) throws JsonProcessingException {
        // REST API 로 결재 서버 내 결재 API 호출 - 상신 가능한지 체크  CREATE, REJECT 리팩토링 대상, 테스트 진행 중
        String companyId = memberLeave.receiveCompanyId();
        String confirmDocumentId = ConfirmDocumentIdGenerator.execute(companyId, vacation.getId());
        ConfirmRaiseRequest confirmRaiseRequest = new ConfirmRaiseRequest(companyId, memberLeave.receiveDepartmentId(), memberLeave.getMemberId());

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(CONFIRM_SERVER_HOST)
                .path("/api/confirm-documents/{confirm-document-id}/raise")
                .buildAndExpand(confirmDocumentId);

        SimpleRestClient simpleRestClient = new SimpleRestClient();
        ResponseResult result = simpleRestClient.post(uriComponents, confirmRaiseRequest, ResponseResult.class);
        return simpleRestClient.convertTo(result, ConfirmDocumentRaiseResponse.class);
    }

//    private ConfirmDocumentRaiseResponse requestVacationRaiseApiV2(Vacation vacation, MemberLeave memberLeave) throws JsonProcessingException {
//        // REST API 로 결재 서버 내 결재 API 호출 - 상신 가능한지 체크  CREATE, REJECT 리팩토링 대상, 테스트 진행 중
//        SimpleRestClient simpleRestClient = new SimpleRestClient();
//        String requestUri = "http://localhost:8000/api/confirm-documents/{confirm-document-id}/raise";
//        String companyId = memberLeave.receiveCompanyId();
//        String confirmDocumentId = ConfirmDocumentIdGenerator.execute(companyId, vacation.getId());
//        ConfirmRaiseRequest confirmRaiseRequest = new ConfirmRaiseRequest(companyId, memberLeave.getOrganization().getDepartmentId(), memberLeave.getMemberId());
//
//        ResponseResult result = simpleRestClient.post(requestUri, confirmRaiseRequest, ResponseResult.class, confirmDocumentId);
//        return simpleRestClient.convertTo(result, ConfirmDocumentRaiseResponse.class);
//    }

    @Transactional
    public VacationServiceResponse cancelVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow();
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow();

        VacationManager vacationManager = VacationManager.updateVacation(vacation, memberLeave);
        vacationManager.validateMemberActive();
        vacationManager.cancel();

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
        vacationManager.update(form.vacationDuration());

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
        List<FamilyOccasionPolicy> policies = familyOccasionPolicyRepository.findByCompanyId(companyId);

        return policies.stream()
                .map(policy -> new FamilyOccasionPolicyResponse(policy.getCompanyId(), policy.getVacationType(), policy.getVacationDay()))
                .toList();
    }

    public List<DepartmentVacationProjection> searchVacations(VacationSearchCondition condition) {
        return vacationDynamicMapper.search(condition);
    }
}
