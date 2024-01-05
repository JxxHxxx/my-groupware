package com.jxx.vacation.api.vacation.presentation;

import com.jxx.vacation.api.vacation.application.VacationService;
import com.jxx.vacation.api.vacation.dto.ApprovalServiceResponse;
import com.jxx.vacation.api.vacation.dto.RequestVacationForm;
import com.jxx.vacation.api.vacation.dto.response.RequestVacationServiceResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;

    @PostMapping("/vacations")
    public ResponseEntity<?> requestVacation(@RequestBody @Validated RequestVacationForm form) {
        RequestVacationServiceResponse response = vacationService.requestVacation(form);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/vacations/{vacation-id}/approval")
    public ResponseEntity<?> requestApproval(@PathVariable(name = "vacation-id") Long vacationId) {
        ApprovalServiceResponse response = vacationService.approval(vacationId);

        return ResponseEntity.ok(response);
    }
}
