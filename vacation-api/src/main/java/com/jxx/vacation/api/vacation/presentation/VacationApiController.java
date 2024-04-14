package com.jxx.vacation.api.vacation.presentation;

import com.jxx.vacation.api.member.application.AuthService;
import com.jxx.vacation.api.member.application.UserSession;
import com.jxx.vacation.api.vacation.application.VacationService;
import com.jxx.vacation.api.vacation.dto.request.ConfirmStatusChangeRequest;
import com.jxx.vacation.api.vacation.dto.request.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.request.VacationTypePolicyForm;
import com.jxx.vacation.api.vacation.dto.response.VacationTypePolicyResponse;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import com.jxx.vacation.api.vacation.dto.response.VacationServiceResponse;
import com.jxx.vacation.api.vacation.query.VacationSearchCondition;
import com.jxx.vacation.core.vacation.projection.DepartmentVacationProjection;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VacationApiController {

    private final VacationService vacationService;
    private final AuthService authService;

    /*** 휴가 작성 API, 결재 서버에는 결재 데이터 생성
     * 결재 서버에서 결재 문서 ID 값을 가져와야 함 */

    @PostMapping("/api/vacations")
    public ResponseEntity<VacationServiceResponse> createVacation(@RequestBody @Validated RequestVacationForm form) {
        VacationServiceResponse response = vacationService.createVacation(form);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/vacations/all")
    public ResponseEntity<?> createVacations(@RequestBody @Validated List<RequestVacationForm> form) {
        vacationService.createVacations(form);

        return ResponseEntity.ok("!");
    }

    /*** 휴가 신청 API, 결재 시스템 UI에 결재 문서가 생성되어 결재자가 볼 수 있도록 함 */

    @PostMapping(value = "/api/vacations/{vacation-id}/raise")
    public ResponseEntity<?> raiseVacation(@PathVariable(name = "vacation-id") Long vacationId) {
        VacationServiceResponse response = vacationService.raiseVacation(vacationId);
        return ResponseEntity.ok(response);
    }

    /*** 결재 수정 API */

    @PatchMapping("/api/vacations/{vacation-id}")
    public ResponseEntity<?> updateVacation(@PathVariable(name = "vacation-id") Long vacationId,
                                            @RequestBody RequestVacationForm requestVacationForm) {
        VacationServiceResponse response = vacationService.updateVacation(vacationId, requestVacationForm);
        return ResponseEntity.ok(response);
    }

    /*** 휴가 취소 API
     * 1. 추가 요구 사항 - 결재가 올라간 휴가를 취소할 경우, 결재 라인에 알려야 한다. */

    @PostMapping("/api/vacations/{vacation-id}/cancel")
    public ResponseEntity<?> cancelVacation(@PathVariable(name = "vacation-id") Long vacationId) {
        VacationServiceResponse response = vacationService.cancelVacation(vacationId);
        return ResponseEntity.ok(response);
    }

    /*** 내 휴가 단일 조회 API */

    @GetMapping("/api/members/{member-id}/vacations/{vacation-id}")
    public ResponseEntity<?> readMyVacation(@PathVariable(name = "member-id") String memberId, @PathVariable(name = "vacation-id") Long vacationId) {
        VacationServiceResponse response = vacationService.readOne(memberId, vacationId);
        return ResponseEntity.ok(response);
    }

    /*** 내 휴가 조회 API */

    @GetMapping("/api/members/{member-id}/vacations")
    public ResponseEntity<?> readMyVacations(@PathVariable(name = "member-id") String memberId) {
        List<VacationServiceResponse> response = vacationService.readByRequesterId(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/vacations/family-occasion-policies")
    public ResponseEntity<?> readFamilyOccasionPolicies(@RequestParam("companyId") String companyId) {
        List<VacationTypePolicyResponse> responses = vacationService.findCompanyVacationTypePolicies(companyId);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/api/vacations")
    public ResponseEntity<?> searchDepartmentVacation(@ModelAttribute VacationSearchCondition searchCondition) {
        List<DepartmentVacationProjection> responses = vacationService.searchVacations(searchCondition);
        return ResponseEntity.ok(responses);
    }

    // TODO only Confirm Server Available, prevent public request
    /**
     * 결재 서버에서 최종 결정권자의 승인, 결정권자의 반려 시, 휴가 서버의 VacationStatus 값을 적절하게 변경해야 한다.
     */
    @PostMapping("/api/vacations/{vacation-id}/vacation-status")
    public ResponseEntity<?> fetchVacationStatus(@PathVariable("vacation-id") Long vacationId,
                                                 @RequestBody ConfirmStatusChangeRequest request) {
        log.info("request from {}", request.requestSystem());
        VacationServiceResponse response = vacationService.fetchVacationStatus(vacationId, request.vacationStatus());
        return ResponseEntity.ok(new ResponseResult<>(200, "요청 완료", response));
    }

    @PostMapping("/api/vacations/set-vacation-type-policy")
    public ResponseEntity<?> setCompanyVacationPolicies(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest) throws IOException {
        UserSession userSession = authService.getUserSession(httpRequest);
        vacationService.setCompanyVacationPolicies(file.getInputStream(), userSession.getMemberId());
        return ResponseEntity.ok(200);
    }

    // JSON 버전
    @PostMapping("/api/vacations/set-vacation-type-policy-v2")
    public ResponseEntity<?> setCompanyVacationPolicies(@RequestBody List<VacationTypePolicyForm> forms, HttpServletRequest httpRequest) throws IOException {
        UserSession userSession = authService.getUserSession(httpRequest);
        vacationService.setCompanyVacationPolicies(forms, userSession.getMemberId());
        return ResponseEntity.ok(200);
    }

}
