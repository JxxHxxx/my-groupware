package com.jxx.vacation.api.vacation.application;

import com.jxx.vacation.api.excel.application.CompanyVacationTypePolicyExcelReader;
import com.jxx.vacation.api.excel.application.ExcelReader;
import com.jxx.vacation.api.vacation.application.function.ConfirmRaiseApiAdapter;
import com.jxx.vacation.api.vacation.dto.request.RequestVacationForm;
import com.jxx.vacation.api.vacation.listener.VacationUpdatedEvent;
import com.jxx.vacation.core.vacation.domain.dto.UpdateVacationDurationForm;
import com.jxx.vacation.core.vacation.domain.dto.UpdateVacationForm;
import com.jxx.vacation.api.vacation.dto.request.VacationTypePolicyForm;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.core.vacation.domain.dto.VacationDurationDto;
import com.jxx.vacation.api.vacation.dto.response.VacationTypePolicyResponse;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.api.vacation.listener.VacationCreatedEvent;
import com.jxx.vacation.api.vacation.query.VacationDynamicMapper;
import com.jxx.vacation.api.vacation.query.VacationSearchCondition;
import com.jxx.vacation.core.common.Creator;
import com.jxx.vacation.core.common.history.History;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import com.jxx.vacation.core.vacation.infra.*;

import com.jxx.vacation.core.vacation.projection.VacationProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiFunction;

import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {

    private final ApplicationEventPublisher eventPublisher;
    private final VacationRepository vacationRepository;
    private final VacationDurationRepository vacationDurationRepository;
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

        VacationManager vacationManager = VacationManager.createVacation(memberLeave, vacationForm.vacationType(), vacationForm.leaveDeduct());

        Vacation vacation = vacationManager.getVacation();
        vacationManager.createVacationDurations(vacation.getVacationType(), vacationForm.requestVacationDurations());
        List<VacationDuration> vacationDurations = vacationManager.getVacationDurations();

        vacationDurationRepository.saveAll(vacationDurations);
        final Vacation savedVacation = vacationRepository.save(vacation);

        List<Vacation> createdVacations = vacationRepository.findAllByRequesterId(requesterId);
        // 신청일이 이미 휴가로 지정되었거나
        vacationManager.validateVacationDatesAreDuplicated(createdVacations);
        // TODO 근무일이 아닐 때 휴가를 신청했는지 검증 (테이블 필드 추가 필요)

        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct())) {
            // TODO 매번 연산하는것보다 DB에 박아버리는것도 나쁘지 않을듯.
            vacationManager.validateRemainingLeaveIsBiggerThanConfirmingVacationsAnd(createdVacations);
        }

        vacationHistRepository.save(new VacationHistory(vacation, History.insert(vacation.getRequesterId())));
        Float totalUseLeaveValue = savedVacation.getTotalUseLeaveValue();
        if (savedVacation.successRequest()) {
            eventPublisher.publishEvent(new VacationCreatedEvent(
                    memberLeave,
                    vacation,
                    totalUseLeaveValue,
                    vacationForm.title(),
                    vacationForm.delegatorId(),
                    vacationForm.delegatorName(),
                    vacationForm.reason())
            );
        }
        return vacationServiceResponse(savedVacation, memberLeave);
    }

    @Transactional
    public void createVacations(List<RequestVacationForm> requestVacationForms) {
        for (RequestVacationForm requestVacationForm : requestVacationForms) {
            createVacation(requestVacationForm);
        }
    }

    /** 수정 시, 메시지 큐 UPDATE 플래그로 날라가야 함 **/
    @Transactional
    public VacationServiceResponse updateVacation(Long vacationId, UpdateVacationForm form) {
        List<Vacation> vacations = vacationRepository.findWithVacationDurations(vacationId);
        if (vacations.isEmpty()) {
            throw new VacationClientException(vacationId + "에 해당하는 정보를 찾을 수 없습니다.");
        }
        Vacation vacation = vacations.get(0);

        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow();

        VacationManager vacationManager = VacationManager.updateVacation(memberLeave, vacation);
        vacationManager.validateUpdatePossible();
        // 수정할 수 있는 정보 - 사유, 대리자, 휴가 기간
        List<UpdateVacationDurationForm> updateVacationDurationForms = form.updateVacationDurationForms();
        // 휴가 기간 업데이트
        for (UpdateVacationDurationForm vdForm : updateVacationDurationForms) {
            VacationDuration findVacationDuration = vacationManager.getVacationDurations().stream()
                    .filter(vd -> vd.identifyVacationDuration(vdForm.vacationDurationId()))
                    .findFirst().orElseThrow(() -> new VacationClientException(
                            "휴가 수정을 할 수 없습니다. 관리자에게 문의해주세요. vacationId: " + vacationId + " vacationDuration:" + vdForm.vacationDurationId()));
            findVacationDuration.updateStartAndEndDateTime(vdForm.startDateTime(), vdForm.endDateTime());
        }
        // N + 1 발생함, persistenceContext 에 수정된 VacationDuration 을 가지고 있기 떄문에 업데이트된 휴가 기간으로 불러옴
        List<Vacation> createdVacations = vacationRepository.findAllByRequesterId(vacation.getRequesterId());

        vacationManager.validateVacationDatesAreDuplicated(createdVacations);
        if (LeaveDeduct.isLeaveDeductVacation(vacation.getLeaveDeduct())) {
            vacationManager.validateRemainingLeaveIsBiggerThanConfirmingVacationsAnd(createdVacations);
        }
        vacationManager.updateLastDuration();

        eventPublisher.publishEvent(new VacationUpdatedEvent(
                form.delegatorId(), form.delegatorName(), form.reason(), vacation, form.contentPk()));

        return vacationServiceResponse(vacation, memberLeave);
    }

    public VacationServiceResponse readOne(String requesterId, Long vacationId) {
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 해당하는 레코드가 존재하지 않습니다."));
        Vacation findVacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 해당하는 레코드가 존재하지 않습니다."));

        List<VacationDurationDto> vacationDurationDto = findVacation.getVacationDurations().stream()
                .map(vd -> new VacationDurationDto(vd.getId(), vd.getStartDateTime(), vd.getEndDateTime(), vd.getUseLeaveValue()))
                .toList();

        return new VacationServiceResponse(
                findVacation.getId(),
                findVacation.getRequesterId(),
                memberLeave.getName(),
                vacationDurationDto,
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
                        vacation.receiveVacationDurationDto(),
                        vacation.getVacationStatus()
                )).toList();
    }

    @Transactional
    public VacationServiceResponse raiseVacation(Long vacationId) {
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter = new ConfirmRaiseApiAdapter();
        return raiseVacation(vacationId, apiAdapter);
    }

    @Transactional
    protected VacationServiceResponse raiseVacation(Long vacationId,
                                                    BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> function) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
        MemberLeave memberLeave = memberLeaveRepository.findMemberWithOrganizationFetch(vacation.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

        VacationManager vacationManager = VacationManager.updateVacation(memberLeave, vacation);
        // validate start
        if (!vacationManager.validateMemberActive()) {
            VacationServiceResponse response = new VacationServiceResponse(vacation.getId(),
                    vacation.getRequesterId(),
                    memberLeave.getName(),
                    vacation.receiveVacationDurationDto(),
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
                vacation.receiveVacationDurationDto(),
                vacation.getVacationStatus());
    }

    @Transactional
    public VacationServiceResponse cancelVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow();
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow();

        VacationManager vacationManager = VacationManager.updateVacation(memberLeave, vacation);
        vacationManager.validateMemberActive();
        // TODO 상신 요청자를 요청자에서 받고 있지 않음, 세션 or Body 받아서 요청자 누군지 검증해야 함 - 일단 vacation 생성자로 ㄱㄱ
        Vacation cancelVacation = vacationManager.cancel();
        vacationHistRepository.save(new VacationHistory(cancelVacation, History.update(vacation.getRequesterId())));


        return vacationServiceResponse(vacation, memberLeave);
    }

    private static VacationServiceResponse vacationServiceResponse(Vacation vacation, MemberLeave memberLeave) {
        return new VacationServiceResponse(
                vacation.getId(),
                vacation.getRequesterId(),
                memberLeave.getName(),
                vacation.receiveVacationDurationDto(),
                vacation.getVacationStatus());
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
                vacation.receiveVacationDurationDto(),
                vacation.getVacationStatus());
    }

    @Transactional
    public void setCompanyVacationPolicies(InputStream inputStream, String memberId) throws IOException {
        ExcelReader<CompanyVacationTypePolicy> excelReader = new CompanyVacationTypePolicyExcelReader(inputStream);
        List<CompanyVacationTypePolicy> vacationTypePolicies = excelReader.readAllRow();
        vacationTypePolicies.stream().forEach(vtp -> vtp.setCreator(new Creator(memberId, "API")));
        companyVacationTypePolicyRepository.saveAll(vacationTypePolicies);
    }

    @Transactional
    public void setCompanyVacationPolicies(List<VacationTypePolicyForm> forms, String memberId) throws IOException {
        List<CompanyVacationTypePolicy> vacationTypePolicies = forms.stream()
                .map(form -> CompanyVacationTypePolicy.builder()
                        .vacationType(VacationType.valueOf(form.vacationType()))
                        .vacationDay(form.vacationDay())
                        .creator(new Creator(memberId, "API"))
                        .companyId(form.companyId())
                        .build())
                .toList();

        companyVacationTypePolicyRepository.saveAll(vacationTypePolicies);
    }

    public List<VacationTypePolicyResponse> findCompanyVacationTypePolicies(String companyId) {
        List<CompanyVacationTypePolicy> policies = companyVacationTypePolicyRepository.findByCompanyId(companyId);

        return policies.stream()
                .map(policy -> new VacationTypePolicyResponse(
                        policy.getCompanyId(),
                        policy.getVacationType(),
                        policy.getVacationDay()))
                .toList();
    }

    public List<VacationProjection> searchVacations(VacationSearchCondition condition) {
        return vacationDynamicMapper.search(condition);
    }

}
