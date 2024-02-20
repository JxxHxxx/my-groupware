package com.jxx.vacation.api.vacation.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.vacation.api.vacation.application.VacationService;
import com.jxx.vacation.api.vacation.dto.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.RequestVacationServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VacationApiController {

    private final VacationService vacationService;

    /**
     * 휴가 작성 API, 결재 서버에는 결재 데이터 생성
     * 결재 서버에서 결재 문서 ID 값을 가져와야 함
     */

    @PostMapping("/api/vacations")
    public ResponseEntity<?> createVacation(@RequestBody @Validated RequestVacationForm form) {
        RequestVacationServiceResponse response = vacationService.createVacation(form);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/vacations/all")
    public ResponseEntity<?> createVacations(@RequestBody @Validated List<RequestVacationForm> form) {
        vacationService.createVacations(form);

        return ResponseEntity.ok("!");
    }


    /**
     * 휴가 신청 API, 결재 시스템 UI에 결재 문서가 생성되어 결재자가 볼 수 있도록 함
     */

    @PostMapping(value = "/api/vacations/{vacation-id}/raise")
    public ResponseEntity<?> raiseVacation(@PathVariable(name = "vacation-id") Long vacationId) throws JsonProcessingException {
        ConfirmDocumentRaiseResponse response = vacationService.raiseVacation(vacationId);

        return ResponseEntity.ok(response);
    }

    /**
     * 결재 수정 API
     */

    @PatchMapping("/api/vacations/{vacation-id}")
    public ResponseEntity<?> updateVacation(@PathVariable(name = "vacation-id") Long vacationId) {

        return ResponseEntity.ok(null);
    }

    /**
     * 휴가 취소 API
     */

    @PostMapping("/api/vacations/{vacation-id}/cancel")
    public ResponseEntity<?> cancelVacation(@PathVariable(name = "vacation-id") Long vacationId) {

        return ResponseEntity.ok(null);
    }

    /**
     * 내 휴가 단일 조회 API
     */

    @GetMapping("/api/members/{member-id}/vacations/{vacation-id}")
    public ResponseEntity<?> readMyVacation(@PathVariable(name = "member-id") Long memberId, @PathVariable(name = "vacation-id") Long vacationId) {

        return ResponseEntity.ok(null);
    }

    /**
     * 내 휴가 조회 API
     */

    @GetMapping("/api/members/{member-id}/vacations")
    public ResponseEntity<?> readMyVacations(@PathVariable(name = "member-id") Long memberId) {

        return ResponseEntity.ok(null);
    }

}
