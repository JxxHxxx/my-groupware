package com.jxx.vacation.batch.application;

import com.jxx.vacation.batch.exception.JxxJobExecutionException;
import com.jxx.vacation.batch.job.parameters.JxxJobParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.jxx.vacation.batch.job.parameters.JxxJobParameter.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutionService {

    private static final String VALIDATOR_BEAN_NAME_SUFFIX = ".parameter-validator";
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final ApplicationContext context;

    public ExitStatus runJob(JobParameters reqeustJobParameters) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        String jobName = null;
        Job job = null;
        try {
            jobName = String.valueOf(reqeustJobParameters.getParameter(JOB_PARMA_JOB_NAME.keyName()).getValue());
            job = context.getBean(jobName, Job.class);
        }
        catch (NullPointerException e) { // jobName = null 일 때
            log.error("required parameter jobName is null");
            throw new JxxJobExecutionException("파라미터 jobName 값이 null 입니다.", e);
        }
        catch (NoSuchBeanDefinitionException e) { // 존재하지 않는 Job 클래스의 Bean 이름일 때
            List<String> jobBeanNames = Arrays
                    .stream(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Job.class))
                    .toList();
            log.error("jobName {} is not existed", jobName, e);
            log.info("jobBeanNameList {}", jobBeanNames);
            throw new JxxJobExecutionException(jobName + " 이름을 가진 Job 타입의 Bean 존재하지 않습니다.", e);
        }

        String jobValidatorBeanName = jobName + VALIDATOR_BEAN_NAME_SUFFIX;
        JobParametersValidator validator = context.getBean(jobValidatorBeanName, JobParametersValidator.class);
        validator.validate(reqeustJobParameters);

        String runId = String.valueOf(reqeustJobParameters.getParameter(JOB_PARAM_RUN_ID.keyName()));

        JobParameters jobParameters = new JobParametersBuilder(reqeustJobParameters, jobExplorer)
                .addJobParameter(JOB_PARAM_RUN_ID.keyName(), runId, String.class, true)
                .toJobParameters();
        log.info("\n=========================================" +
                "\nTry job name {} " +
                "\nExecute by API & run.id {}" +
                "\n=========================================", job.getName(), runId);

        return jobLauncher.run(job, jobParameters)
                .getExitStatus();
    }
}
