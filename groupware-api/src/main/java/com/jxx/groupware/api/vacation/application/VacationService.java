package com.jxx.groupware.api.vacation.application;

import com.jxx.groupware.api.excel.application.CompanyVacationTypePolicyExcelReader;
import com.jxx.groupware.api.excel.application.ExcelReader;
import com.jxx.groupware.api.vacation.application.function.ConfirmCancelApiAdapter;
import com.jxx.groupware.api.vacation.application.function.ConfirmRaiseApiAdapter;
import com.jxx.groupware.api.vacation.dto.request.RequestVacationForm;
import com.jxx.groupware.api.vacation.dto.response.ConfirmDocumentCancelResponse;
import com.jxx.groupware.api.vacation.listener.VacationUpdatedEvent;
import com.jxx.groupware.core.vacation.domain.dto.RequestVacationDuration;
import com.jxx.groupware.core.vacation.domain.dto.UpdateVacationForm;
import com.jxx.groupware.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.groupware.core.vacation.domain.dto.VacationDurationDto;
import com.jxx.groupware.api.vacation.dto.response.VacationTypePolicyResponse;
import com.jxx.groupware.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.groupware.api.vacation.listener.VacationCreatedEvent;
import com.jxx.groupware.api.vacation.query.VacationDynamicMapper;
import com.jxx.groupware.api.vacation.query.VacationSearchCondition;
import com.jxx.groupware.core.common.Creator;
import com.jxx.groupware.core.common.history.History;
import com.jxx.groupware.core.vacation.domain.entity.*;
import com.jxx.groupware.core.vacation.domain.exeception.VacationClientException;
import com.jxx.groupware.core.vacation.domain.service.VacationTypePolicyValidator;
import com.jxx.groupware.core.vacation.infra.*;

import com.jxx.groupware.core.vacation.projection.VacationProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import static com.jxx.groupware.core.message.body.vendor.confirm.ConfirmStatus.*;
import static com.jxx.groupware.core.vacation.domain.entity.VacationStatus.*;

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
    private final PlatformTransactionManager platformTransactionManager;
    /**
     * 트랜잭션 커밋/롤백 정책
     * 복구불가능한 비즈니스 로직의 경우 VacationClientException <- 롤백
     * 복구 가능한 경우, 다른 예외를 사용
     */
    @Transactional
    public VacationServiceResponse createVacation(RequestVacationForm vacationForm) {
        String requesterId = vacationForm.requesterId();
        // 사용자 검증
        MemberLeave memberLeave = memberLeaveRepository.findMemberLeaveByMemberId(requesterId)
                .orElseThrow(() -> new VacationClientException("requesterId " + requesterId + " not found", requesterId));
        String memberCompanyId = memberLeave.receiveCompanyId();

        // 경조사일 경우, 경조사 정책 검증
        if (VacationType.isSpecialVacationType(vacationForm.vacationType())) {
            List<CompanyVacationTypePolicy> companyVacationTypePolicies = companyVacationTypePolicyRepository
                    .findByCompanyId(memberCompanyId);
            VacationTypePolicyValidator vacationTypePolicyValidator = new VacationTypePolicyValidator(companyVacationTypePolicies);
            for (RequestVacationDuration vacationDuration : vacationForm.requestVacationDurations()) {
                vacationTypePolicyValidator.validate(vacationForm.vacationType(), memberCompanyId, vacationDuration);
            }
        }

        VacationManager vacationManager = VacationManager.createVacation(memberLeave, vacationForm.vacationType(), vacationForm.leaveDeduct());

        Vacation vacation = vacationManager.getVacation();
        // 임시 로직 - 휴가(반차X)시 시간 조정
        vacationForm.requestVacationDurations()
                .forEach(vd -> vd.reconcliation(vacation.getVacationType()));

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
        List<RequestVacationDuration> updateVacationDurations = form.requestVacationDurations();

        // 기존 휴가 기간을 업데이트하는 파라미터
        List<RequestVacationDuration> existedVacationDurationUpdated = updateVacationDurations.stream()
                .filter(vd -> Objects.nonNull(vd.getVacationDurationId()))
                .toList();
        // 휴가 기간 업데이트 -> 기존 기간 수정 / 신규 기간 추가 CASE
        List<VacationDuration> vacationDurations = vacationManager.getVacationDurations();
        for (VacationDuration vacationDuration : vacationDurations) {
            RequestVacationDuration updateVacationDuration = existedVacationDurationUpdated.stream()
                    .filter(exvd -> Objects.equals(exvd.getVacationDurationId(), vacationDuration.getId()))
                    .findFirst().orElseThrow(() -> new VacationClientException("알 수 없는 에러"));
            // 더티 체킹 발생
            vacationDuration.updateStartAndEndDateTime(updateVacationDuration.getStartDateTime(), updateVacationDuration.getEndDateTime());
        }

        // 신규 휴가 기간을 업데이트하는 파라미터 vacationDurationId 가 null 인 경우가 신규라고 약속
        List<RequestVacationDuration> newVacationDurationUpdated = updateVacationDurations.stream()
                .filter(vd -> Objects.isNull(vd.getVacationDurationId()))
                .toList();

        List<VacationDuration> newVacationDurations = newVacationDurationUpdated.stream()
                .map(nvd -> {
                    VacationDuration vacationDuration = new VacationDuration(
                            nvd.getStartDateTime(), nvd.getEndDateTime(), vacation.getLeaveDeduct(), vacation.vacationType());
                    vacationDuration.mappingVacation(vacation);
                    return vacationDuration;
                })
                .toList();

        // 이후 vacationDuration id 가지고 작업을 해야되서 Flush 해야됨
        vacationDurationRepository.saveAllAndFlush(newVacationDurations);

        // 기존 휴가들과의 검증이 필요함.
        List<Vacation> createdVacations = vacationRepository.fetchAllByRequesterId(vacation.getRequesterId());
        // 검증
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

    public VacationServiceResponse raiseVacationV2(Long vacationId) {
        BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter = new ConfirmRaiseApiAdapter();
        return raiseVacationV2(vacationId, apiAdapter);
    }
    protected VacationServiceResponse raiseVacationV2(Long vacationId, BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> apiAdapter) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
        MemberLeave memberLeave = memberLeaveRepository.findMemberWithOrganizationFetch(vacation.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));

        VacationManager vacationManager = VacationManager.updateVacation(memberLeave, vacation);
        // 이 부분도 개선해야함 응답받은 사람은 왜 실패했는지 모를수도 있음
        if (!vacationManager.validateMemberActive()) {
            VacationServiceResponse response = new VacationServiceResponse(vacation.getId(),
                    vacation.getRequesterId(),
                    memberLeave.getName(),
                    vacation.receiveVacationDurationDto(),
                    vacation.getVacationStatus());
            return response;
        }
        // 결재 서버에 요청이 가능한 휴가인지 검증
        vacationManager.isRaisePossible();
        // 결재 서버 API 호출
        ConfirmDocumentRaiseResponse response = apiAdapter.apply(vacation, memberLeave);
        // 트랜잭션 시작
        TransactionStatus txStatus = platformTransactionManager.getTransaction(TransactionDefinition.withDefaults());
        try {
            Vacation riseVacation = vacationManager.raise(response.confirmStatus());
            vacationHistRepository.save(new VacationHistory(riseVacation, History.update(vacation.getRequesterId())));
        } catch (Exception e) {
            platformTransactionManager.rollback(txStatus);
            log.warn("처리중에 오류 발생 롤백합니다.", e);
            // 이거 커스텀 예외 클래스 만들어서 처리하자.
            throw new RuntimeException(e);
        }
        platformTransactionManager.commit(txStatus);

        return new VacationServiceResponse(vacation.getId(),
                vacation.getRequesterId(),
                memberLeave.getName(),
                vacation.receiveVacationDurationDto(),
                vacation.getVacationStatus());
    }

    public VacationServiceResponse cancelVacation(Long vacationId) {
        ConfirmCancelApiAdapter confirmCancelApiAdapter = new ConfirmCancelApiAdapter();
        return cancelVacation(vacationId, confirmCancelApiAdapter);
    }

    protected VacationServiceResponse cancelVacation(Long vacationId,
                                                     BiFunction<Vacation, MemberLeave, ConfirmDocumentCancelResponse> apiAdapter) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow();
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow();
        // 취소 가능한 상태인지 검증
        VacationManager vacationManager = VacationManager.updateVacation(memberLeave, vacation);
        vacationManager.validateMemberActive();
        // 타 시스템 결재 서버 취소 API 호출
        ConfirmDocumentCancelResponse response = apiAdapter.apply(vacation, memberLeave);

        // 트랜잭션 시작
        TransactionStatus txStatus = platformTransactionManager.getTransaction(TransactionDefinition.withDefaults());
        try {
            if (CANCEL.equals(response.confirmStatus())) {
                Vacation cancelVacation = vacationManager.cancel();
                vacationHistRepository.save(new VacationHistory(cancelVacation, History.update(vacation.getRequesterId())));
            }
        } catch (Exception e) {
            platformTransactionManager.rollback(txStatus);
            log.error("DB 통신 과정중에 오류가 발생하여 롤백합니다.");
            throw new RuntimeException(e);
        }
        platformTransactionManager.commit(txStatus);
        // 트랜잭션 종료
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
        if (!(vacationStatus.equals(APPROVED) || vacationStatus.equals(VacationStatus.REJECT)))
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

    public List<VacationTypePolicyResponse> findCompanyVacationTypePolicies(String companyId) {
        List<CompanyVacationTypePolicy> policies = companyVacationTypePolicyRepository.findByCompanyId(companyId);

        return policies.stream()
                .map(policy -> new VacationTypePolicyResponse(
                        policy.getCompanyId(),
                        policy.getVacationType(),
                        policy.getVacationTypeName(),
                        policy.getVacationDay()))
                .toList();
    }

    public List<VacationProjection> searchVacations(VacationSearchCondition condition) {
        return vacationDynamicMapper.search(condition);
    }

}
