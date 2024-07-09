package com.jxx.vacation.batch.presentation;

import com.jxx.vacation.batch.application.JobConfigService;
import com.jxx.vacation.batch.dto.request.EnrollJobForm;
import com.jxx.vacation.batch.dto.request.JobHistoryCond;
import com.jxx.vacation.batch.dto.request.ScheduleJobUpdateRequest;
import com.jxx.vacation.batch.dto.response.EnrollJobResponse;
import com.jxx.vacation.batch.dto.response.JobHistoryResponse;
import com.jxx.vacation.batch.dto.response.JobMetadataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobConfigApiController {

    private final JobConfigService jobConfigService;

    @PostMapping("/admin/batch/jobs")
    public ResponseEntity<?> enrollJobInformation(@RequestBody EnrollJobForm enrollJobForm) {
        EnrollJobResponse responses = jobConfigService.enrollBatchJob(enrollJobForm);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/batch/jobs")
    public ResponseEntity<?> getJobInformation() {
        List<JobMetadataResponse> responses = jobConfigService.findAllJob();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/batch/jobs-hist")
    public ResponseEntity<?> getJobs(@RequestParam("page") int page, @RequestParam int size,
                                     @ModelAttribute @Validated JobHistoryCond cond) {
        Page<JobHistoryResponse> responses = jobConfigService.pageJobHistories(cond, page, size);
        return ResponseEntity.ok(responses);
    }

    // 스케줄 시간 갱신 API
    @PatchMapping("/admin/batch/jobs")
    public ResponseEntity<?> reschedule(@RequestBody  ScheduleJobUpdateRequest request) {
        jobConfigService.rescheduleBatchJob(request);
        return ResponseEntity.ok("");
    }
}
