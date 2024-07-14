package com.jxx.groupware.api.vacation.presentation;

import com.jxx.groupware.api.member.application.AuthService;
import com.jxx.groupware.api.member.application.UserSession;
import com.jxx.groupware.api.vacation.application.VacationAdminService;
import com.jxx.groupware.api.vacation.dto.request.CommonVacationForm;
import com.jxx.groupware.api.vacation.dto.request.CommonVacationServiceForm;
import com.jxx.groupware.api.vacation.dto.request.CompanyVacationTypePolicyForm;
import com.jxx.groupware.api.vacation.dto.request.CompanyVacationTypePolicyRequest;
import com.jxx.groupware.api.vacation.dto.response.CommonVacationServiceResponse;
import com.jxx.groupware.api.vacation.dto.response.VacationTypePolicyResponse;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 공동 연차는 설정 시, 차감되는 걸로 정책 수립
// 관리자 로직은 추후 처리 예정

@Slf4j
@RestController
@RequiredArgsConstructor
public class VacationAdminController {

    private final VacationAdminService vacationAdminService;
    private final AuthService authService;

    // 공동 연차 지정 API
    @PostMapping("/admin/vacations/set-common-vacation")
    public ResponseEntity<?> setCommonVacation(@RequestBody CommonVacationForm vacationForm, HttpServletRequest httpRequest) {
        log.info("공동 연차를 등록합니다.");
        UserSession userSession = authService.getUserSession(httpRequest);
        CommonVacationServiceForm vacationServiceForm = new CommonVacationServiceForm(userSession, vacationForm);
        CommonVacationServiceResponse response = vacationAdminService.assignCommonVacation(vacationServiceForm);
        return ResponseEntity.ok(response);
    }
    // 공동 연차 수정 API
    @PatchMapping("/admin/vacations/update-common-vacation")
    public ResponseEntity<?> updateCommonVacation() {
        return null;
    }

    // 연차 충전 2024 -> 2025년으로 갔을 때 -> 이건 배치로 가야될듯.

    //
    @PostMapping("/admin/vacations/family-occasion-policies")
    public ResponseEntity<?> addCompanyVacationTypePolicies(@RequestBody List<CompanyVacationTypePolicyForm> form, HttpServletRequest httpRequest) {
        UserSession userSession = authService.getUserSession(httpRequest);
        CompanyVacationTypePolicyRequest request = new CompanyVacationTypePolicyRequest(userSession.getMemberId(), form);
        // 관리자 처리 로직
        List<VacationTypePolicyResponse> responses = vacationAdminService.addCompanyVacationTypePolicies(request);

        return ResponseEntity.ok(new ResponseResult<>(200, "경조 정책 생성 완료", responses));
    }
}
