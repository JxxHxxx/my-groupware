package com.jxx.vacation.batch.application;

import com.jxx.vacation.batch.domain.AdminClientException;
import com.jxx.vacation.batch.domain.JobMetaData;
import com.jxx.vacation.batch.domain.JobParam;
import com.jxx.vacation.batch.dto.request.EnrollJobForm;
import com.jxx.vacation.batch.dto.request.EnrollJobParam;
import com.jxx.vacation.batch.dto.request.JobHistoryCond;
import com.jxx.vacation.batch.dto.response.EnrollJobResponse;
import com.jxx.vacation.batch.dto.response.JobHistoryResponse;
import com.jxx.vacation.batch.dto.response.JobMetadataResponse;
import com.jxx.vacation.batch.dto.response.JobParamResponse;
import com.jxx.vacation.batch.infra.JobCustomMapper;
import com.jxx.vacation.batch.infra.JobMetaDataRepository;
import com.jxx.vacation.batch.infra.JobParamRepository;
import com.jxx.vacation.core.common.pagination.PageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class JobConfigService {

    private final JobMetaDataRepository jobMetaDataRepository;
    private final JobParamRepository jobParamRepository;
    private final ApplicationContext appContext;
    private final JobCustomMapper jobCustomMapper;

    // create

    @Transactional
    public EnrollJobResponse enrollBatchJob(EnrollJobForm form) {
        BeanFactory beanFactory = appContext.getAutowireCapableBeanFactory();
        // 애플리케이션에 등록된 jobName 이 아닐 경우 리턴 타입이 Job인 걸로 검증해야 더 정확함
        String jobBeanName = form.jobName();
        if (!beanFactory.containsBean(jobBeanName)) {
            log.error("jobName:{}는 애플리케이션 상에 등록된 JobBean 이 아닙니다.", jobBeanName);
            throw new AdminClientException(jobBeanName + "에 등록되지 않은 JobBean 입니다.");
        }

        boolean presentJobName = jobMetaDataRepository.findByJobName(form.jobName()).isPresent();
        if (presentJobName) {
            log.error("JobName:{} 은 이미 저장되어 있습니다.", form.jobName());
            throw new AdminClientException("JobName:" + form.jobName() + " 은 이미 저장되어 있습니다.");
        }

        JobMetaData jobMetaData = JobMetaData.builder()
                .jobName(form.jobName())
                .jobDescription(form.jobDescription())
                .used(form.used())
                .enrolledTime(LocalDateTime.now())
                .executionType(form.executeType())
                .executionTime(form.executionTime())
                .executionDuration(form.executionDuration())
                .build();

        List<JobParam> jobParams = form.jobParams()
                .stream().map(jp -> {
                            JobParam jobParam = new JobParam(jp.required(),
                                    jp.parameterKey(),
                                    jp.paramDescription(),
                                    jp.placeHolder(),
                                    jobMetaData);
                            jobParam.mappedJobMetaData(jobMetaData);
                            return jobParam;
                        }
                ).toList();
        JobMetaData savedJobMetaData;
        try {
            savedJobMetaData = jobMetaDataRepository.save(jobMetaData);
            jobParamRepository.saveAll(jobParams);
        } catch (Exception e) {
            log.error("Data Access Error", e);
            throw new RuntimeException(e);
        }
        List<EnrollJobParam> enrollJobParams = savedJobMetaData.getJobParams().stream().map(
                jobParam -> new EnrollJobParam(jobParam.getParameterKey(),
                        jobParam.getParamDescription(),
                        jobParam.getPlaceHolder(),
                        jobParam.isRequired())
        ).toList();
        return new EnrollJobResponse(
                savedJobMetaData.getPk(),
                savedJobMetaData.getJobName(),
                savedJobMetaData.getJobDescription(),
                savedJobMetaData.getExecutionType(),
                savedJobMetaData.getEnrolledTime(),
                savedJobMetaData.getExecutionTime(),
                savedJobMetaData.getExecutionDuration(),
                enrollJobParams);
    }

    public List<JobMetadataResponse> findAllJob() {
        List<JobMetaData> jobMetaData = jobMetaDataRepository.fetchAllWithJobParams();
        return jobMetaData.stream().map(job -> new JobMetadataResponse(
                        job.getJobName(),
                        job.getJobDescription(),
                        job.isUsed(),
                        job.getExecutionTime(),
                        job.getJobParams().stream().map(param -> new JobParamResponse(
                                param.getParameterKey(),
                                param.getParamDescription(),
                                param.getPlaceHolder(),
                                param.isRequired())).toList()))
                .toList();
    }

    // update
    @Transactional
    public void updateBatchJob() {

    }
    // read
    public Page<JobHistoryResponse> pageJobHistories(JobHistoryCond cond, int page, int size) {
        List<JobHistoryResponse> jobHistories = jobCustomMapper.findJobExecutionHistory(cond);
        PageService pageService = new PageService(page, size);
        return pageService.convertToPage(jobHistories);

    }
}
