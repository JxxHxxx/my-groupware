package com.jxx.vacation.batch.presentation;

import com.jxx.vacation.batch.application.JobExecutionService;
import com.jxx.vacation.batch.dto.JobLauncherRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JobExecutionApiController {

    private final JobExecutionService jobExecutionService;

    @PostMapping("/batch/job/run")
    public ResponseEntity<?> runJob(@RequestBody JobLauncherRequest request) throws Exception{
        ExitStatus exitStatus = jobExecutionService.runJob(request.getJobParameters());
        return ResponseEntity.ok(exitStatus.toString());
    }
}
