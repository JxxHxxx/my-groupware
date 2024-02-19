package com.jxx.vacation.api.vacation.application;

import com.jxx.vacation.api.vacation.dto.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.RequestVacationServiceResponse;
import com.jxx.vacation.core.message.*;
import com.jxx.vacation.core.message.payload.approval.DocumentType;
import com.jxx.vacation.core.message.payload.approval.form.VacationApprovalForm;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Organization;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import com.jxx.vacation.core.vacation.domain.exeception.UnableToApplyVacationException;
import com.jxx.vacation.core.vacation.domain.service.VacationCalculator;
import com.jxx.vacation.core.vacation.infra.MemberLeaveRepository;
import com.jxx.vacation.core.vacation.infra.VacationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.jxx.vacation.core.message.payload.approval.DocumentType.*;
import static com.jxx.vacation.core.vacation.domain.entity.VacationStatus.*;

// TODO 비즈니스 로직, 엔티티 혹은 도메인 서비스 클래스로 이동 예정

@Slf4j
@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final MemberLeaveRepository memberLeaveRepository;
    private final MessageQRepository messageQRepository;

    // create
    @Transactional
    public RequestVacationServiceResponse createVacation(RequestVacationForm form) {
        log.info("type {} date {}", form.vacationDuration().getVacationType(), form.vacationDuration().getEndDateTime());
        Vacation vacation = Vacation.createVacation(form.requestId(), form.vacationDuration());

        String requesterId = vacation.getRequesterId();
        //사용자 및 조직 활성화 여부 검증
        MemberLeave findMemberLeave = validateActiveMemberLeave(vacation, requesterId);
        Organization organization = validateActiveOrganization(vacation, findMemberLeave);

        vacation.validateDeductedLeave(); // 신청한 휴가가 연차에서 차감해야 하는 휴가인지 체크 및 차감 플래그 설정

        // 차감해야하는 연차 일수 계산
        float vacationDate = checkRemainingLeaveIsBiggerThan(vacation, findMemberLeave);

        final Vacation savedVacation = vacationRepository.save(vacation);// 저장

        if (vacation.isFailVacationStatus()) {
            return createVacationServiceResponse(savedVacation, requesterId, findMemberLeave);
        }
        // 메시지 로직 시작 - 리팩터링 대상
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
        // 메시지 로직 끝 - 리팩터링 대상

        return createVacationServiceResponse(savedVacation, requesterId, findMemberLeave);
    }

    private static RequestVacationServiceResponse createVacationServiceResponse(Vacation savedVacation, String requesterId, MemberLeave findMemberLeave) {
        return new RequestVacationServiceResponse(
                savedVacation.getId(),
                requesterId,
                findMemberLeave.getName(),
                savedVacation.getVacationDuration(),
                savedVacation.getVacationStatus());
    }

    private static float checkRemainingLeaveIsBiggerThan(Vacation createVacation, MemberLeave findMemberLeave) {
        float vacationDuration = 0F;
        try {
            vacationDuration = VacationCalculator.getVacationDuration(createVacation);
            findMemberLeave.checkRemainingLeaveIsBiggerThan(vacationDuration); // 잔여 연차가 차감되는 연차보다 큰지 검증
        } catch (UnableToApplyVacationException e) {
            log.warn("MESSAGE : {}", e.getMessage(), e);
            createVacation.changeVacationStatus(FAIL);
        }

        return vacationDuration;
    }

    private Organization validateActiveOrganization(Vacation requestVacation, MemberLeave findMemberLeave) {
        Organization organization = findMemberLeave.getOrganization();

        try {
            organization.checkActive();
        } catch (InactiveException e) {
            log.info("COMPANY ID : {} ORG ID {} MESSAGE : {}", organization.getCompanyId(), organization.getDepartmentId(),
                    e.getMessage(), e);
            requestVacation.changeVacationStatus(FAIL);
        }
        return organization;
    }

    private MemberLeave validateActiveMemberLeave(Vacation requestVacation, String requesterId) {
        MemberLeave findMemberLeave = memberLeaveRepository.findMemberLeaveByMemberId(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 해당하는 레코드가 존재하지 않습니다."));

        try {
            findMemberLeave.checkActive();
        } catch (InactiveException e) {
            log.info("REQUEST ID : {} MESSAGE : {}", requesterId, e.getMessage(), e);
            requestVacation.changeVacationStatus(FAIL);
        }
        return findMemberLeave;
    }

    @Transactional
    public ConfirmDocumentRaiseResponse raiseVacation(Long vacationId) {
        //유효성 검증
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new IllegalArgumentException("조건에 해당하는 레코드가 존재하지 않습니다."));

        MemberLeave findMemberLeave = validateActiveMemberLeave(vacation, vacation.getRequesterId());
        validateActiveOrganization(vacation, findMemberLeave);

        checkRemainingLeaveIsBiggerThan(vacation, findMemberLeave);
        //유효성 검증

        //메시지 payload 생성
        VacationStatus vacationStatus = vacation.getVacationStatus();
        if (!(CREATE.equals(vacationStatus) || REJECT.equals(vacationStatus))) {
            throw new IllegalArgumentException("이미 결재가 올라갔거나 종료된 휴가입니다.");
        }

        // REST API 로 결재 서버 내 결재 API 호출 - 상신 가능한지 체크  CREATE, REJECT 리팩토링 대상, 테스트 진행 중
        RestTemplate restTemplate = new RestTemplate();

        String confirmDocumentId = "VAC" + vacation.getId();

        // TODO 결재 상신 REST API 변경 되었음
        ConfirmDocumentRaiseResponse response = restTemplate.postForObject(
                "http://localhost:8010/api/confirm-documents/raise?cdid=" + confirmDocumentId, null, ConfirmDocumentRaiseResponse.class);
        log.info("confirmDocument {}", response);
        // REST API 로 결재 서버 내 결재 API 호출 - 상신 가능한지 체크  CREATE, REJECT 리팩토링 대상, 테스트 진행 중

        // 정상 응답 시, 아래 코드 반영, 아닐 시 철회
        if ("RAISE".equals(response.confirmStatus())) {
            vacation.changeVacationStatus(REQUEST);
        }
        else {
            throw new IllegalArgumentException("결재 서버와 통신이 실패하였습니다. 혹은 결재 요청이 진행되지 않았습니다.");
        }

        return response;

    }
}
