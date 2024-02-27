package com.jxx.vacation.api.vacation.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.vacation.api.common.web.SimpleRestClient;
import com.jxx.vacation.api.vacation.dto.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.request.ConfirmRaiseRequest;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import com.jxx.vacation.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.vacation.core.message.*;
import com.jxx.vacation.core.message.payload.approval.form.VacationApprovalForm;
import com.jxx.vacation.core.vacation.domain.entity.*;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jxx.vacation.core.message.payload.approval.DocumentType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final MemberLeaveRepository memberLeaveRepository;
    private final MessageQRepository messageQRepository;

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

    // create
    @Transactional
    public VacationServiceResponse createVacation(RequestVacationForm vacationForm) {
        String requesterId = vacationForm.requesterId();
        MemberLeave memberLeave = memberLeaveRepository.findMemberLeaveByMemberId(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 해당하는 레코드가 존재하지 않습니다."));

        VacationManager vacationManager = VacationManager.createVacation(vacationForm.vacationDuration(), memberLeave);
        vacationManager.validateVacation();
        Vacation vacation = vacationManager.getVacation();

        final Vacation savedVacation = vacationRepository.save(vacation);

        if (savedVacation.isFailRequest()) { // Queue 가 결재 서버에 전달되지 않도록 여기서 리턴
            return createVacationServiceResponse(savedVacation, memberLeave);
        }

        Organization organization = memberLeave.getOrganization();
        float vacationDate = vacationManager.receiveVacationDate();
        // 메시지 로직 시작 - 이벤트 리스너 처리할지 고민중
        VacationApprovalForm vacationApprovalForm = VacationApprovalForm.create(
                requesterId, organization.getCompanyId(), organization.getDepartmentId(),
                "3rd-vacations", VAC, vacationDate, vacation.getId());

        Map<String, Object> messageBody = MessageBodyBuilder.createVacationApprovalBody(vacationApprovalForm);
        MessageQ messageQ = MessageQ.builder()
                .messageDestination(MessageDestination.APPROVAL)
                .messageProcessStatus(MessageProcessStatus.SENT)
                .body(messageBody)
                .build();

        messageQRepository.save(messageQ);
        return createVacationServiceResponse(savedVacation, memberLeave);
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
        vacationManager.validateVacation();

        vacationManager.isRaisePossible(); // 상신이 가능한 상태이면.
        ConfirmDocumentRaiseResponse response = requestVacationRaiseApi(vacation, memberLeave); // 외부 통신
        vacationManager.raise(response.confirmStatus());

        return response;

    }

    private ConfirmDocumentRaiseResponse requestVacationRaiseApi(Vacation vacation, MemberLeave memberLeave) throws JsonProcessingException {
        // REST API 로 결재 서버 내 결재 API 호출 - 상신 가능한지 체크  CREATE, REJECT 리팩토링 대상, 테스트 진행 중
        SimpleRestClient simpleRestClient = new SimpleRestClient();
        String requestUri = "http://localhost:8000/api/confirm-documents/{confirm-document-pk}/raise";
        String companyId = memberLeave.receiveCompanyId();
        String confirmDocumentId = ConfirmDocumentIdGenerator.execute(companyId, vacation.getId());
        ConfirmRaiseRequest confirmRaiseRequest = new ConfirmRaiseRequest(companyId, memberLeave.getOrganization().getDepartmentId(), memberLeave.getMemberId());

        ResponseResult result = simpleRestClient.post(requestUri, confirmRaiseRequest, ResponseResult.class, confirmDocumentId);
        return simpleRestClient.convertTo(result, ConfirmDocumentRaiseResponse.class);
    }

    @Transactional
    public VacationServiceResponse cancelVacation(Long vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow();
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow();

        VacationManager vacationManager = VacationManager.updateVacation(vacation, memberLeave);
        vacationManager.validateVacation();
        vacationManager.cancel();

        return createVacationServiceResponse(vacation, memberLeave);
    }


    @Transactional
    public VacationServiceResponse updateVacation(Long vacationId, RequestVacationForm form) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow();
        MemberLeave memberLeave = memberLeaveRepository.findByMemberId(vacation.getRequesterId())
                .orElseThrow();

        VacationManager vacationManager = VacationManager.updateVacation(vacation, memberLeave);
        vacationManager.validateVacation();
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
}
