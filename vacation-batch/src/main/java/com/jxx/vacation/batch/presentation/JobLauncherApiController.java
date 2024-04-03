package com.jxx.vacation.batch.presentation;

import com.jxx.vacation.batch.dto.JobLauncherRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.jxx.vacation.batch.job.common.JobParameterConst.JOB_PARAM_RUN_ID;
import static com.jxx.vacation.batch.job.common.JobParameterConst.JOB_PARMA_JOB_NAME;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JobLauncherApiController {
    private final JobLauncher jobLauncher;

    private final JobExplorer jobExplorer;
    private final ApplicationContext context;

    @PostMapping("/batch/job/run")
    public ResponseEntity<?> runJob(@RequestBody JobLauncherRequest request) throws Exception{
        String jobName = String.valueOf(request.getJobParameters().getParameter(JOB_PARMA_JOB_NAME).getValue());
        Job job = context.getBean(jobName, Job.class);
        // 이 부분 분리 해야 좋을 듯 START
        JobParameters findJobParameters = request.getJobParameters();

        JobParametersValidator validator = context
                .getBean(jobName + ".parameter-validator", JobParametersValidator.class);
        validator.validate(findJobParameters);

        String runId = String.valueOf(findJobParameters.getParameter(JOB_PARAM_RUN_ID));

        JobParameters jobParameters = new JobParametersBuilder(findJobParameters, jobExplorer)
                .addJobParameter("run.id",runId, String.class, true)
                .toJobParameters();
        // 이 부분 분리 해야 좋을 듯 END

        log.info("\n=========================================" +
                "\nTry job name {} " +
                "\nExecute by API & run.id {}" +
                "\n=========================================", job.getName(), runId);

        ExitStatus exitStatus = jobLauncher.run(job, jobParameters)
                .getExitStatus();
        return ResponseEntity.ok(exitStatus.toString());
    }
}
