package com.jxx.vacation.batch.presentation;

import com.jxx.vacation.batch.application.JobConfigService;
import com.jxx.vacation.batch.dto.request.EnrollJobForm;
import com.jxx.vacation.batch.dto.response.EnrollJobResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobConfigApiController {

    private final JobConfigService jobConfigService;

    @PostMapping("/admin/job-configurations")
    public ResponseEntity<?> enrollJobInformation(@RequestBody EnrollJobForm enrollJobForm) {
        EnrollJobResponse responses = jobConfigService.enrollBatchJob(enrollJobForm);
        return ResponseEntity.ok(responses);
    }
}
