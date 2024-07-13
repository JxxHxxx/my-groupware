package com.jxx.vacation.batch.application;

import com.jxx.vacation.batch.domain.AdminClientException;
import com.jxx.vacation.batch.domain.JobMetaData;
import com.jxx.vacation.batch.domain.JobParam;
import com.jxx.vacation.batch.dto.request.*;
import com.jxx.vacation.batch.dto.response.*;
import com.jxx.vacation.batch.infra.JobCustomMapper;
import com.jxx.vacation.batch.infra.JobMetaDataRepository;
import com.jxx.vacation.batch.infra.JobParamRepository;
import com.jxx.vacation.batch.infra.QuartzExploreMapper;
import com.jxx.vacation.core.common.pagination.PageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.springframework.scheduling.support.CronExpression.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class JobConfigService {

    private final JobMetaDataRepository jobMetaDataRepository;
    private final JobParamRepository jobParamRepository;
    private final ApplicationContext appContext;
    private final JobCustomMapper jobCustomMapper;
    private final QuartzExploreMapper quartzExploreMapper;
    private final Scheduler scheduler;

    private static final String TRIGGER_NAME_SUX = ".trigger";
    private static final String TRIGGER_GROUP_SUX = ".trigger.group";
    // create

    //TODO 트리거 추가로 이에 맞게 수정해야함
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
                .enrolledTime(LocalDateTime.now())
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
                savedJobMetaData.getEnrolledTime(),
                enrollJobParams);
    }

    public List<JobMetadataResponse> findAllJob() {
        List<JobMetaData> jobMetaData = jobMetaDataRepository.fetchAllWithJobParams();
        return jobMetaData.stream().map(job -> new JobMetadataResponse(
                        job.getJobName(),
                        job.getJobDescription(),
                        job.getJobParams().stream().map(param -> new JobParamResponse(
                                param.getParameterKey(),
                                param.getParamDescription(),
                                param.getPlaceHolder(),
                                param.isRequired())).toList()))
                .toList();
    }

    // read
    public Page<JobHistoryResponse> pageJobHistories(JobHistoryCond cond, int page, int size) {
        List<JobHistoryResponse> jobHistories = jobCustomMapper.findJobExecutionHistory(cond);
        PageService pageService = new PageService(page, size);
        return pageService.convertToPage(jobHistories);
    }

    @Transactional
    public TriggerCreateResponse createTrigger(TriggerCreateRequest request) {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(request.cronExpression());
        String triggerName = request.jobName() + TRIGGER_NAME_SUX;
        String triggerGroup = request.jobName() + TRIGGER_GROUP_SUX;
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(triggerName, triggerGroup))
                .forJob(request.jobName())
                .withSchedule(cronScheduleBuilder)
                .build();

        // 트리거를 저장한다.
        try {
            scheduler.scheduleJob(cronTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        TriggerKey triggerKey = cronTrigger.getKey();
        return new TriggerCreateResponse(
                triggerKey.getName(),
                triggerKey.getGroup(),
                cronTrigger.getCronExpression(),
                cronTrigger.getJobKey().getName());
    }

    @Transactional
    public void rescheduleTrigger(ScheduleJobUpdateRequest request) {
        String cronExp = request.getCronExpression();
        if (!CronExpression.isValidExpression(cronExp)) {
            throw new AdminClientException("잘못된 크론 표현식 입니다.", "AC01");
        }

        CronTriggerResponse cronTriggerResponse = quartzExploreMapper.findByGroupName(request.getTriggerGroup());
        if (Objects.isNull(cronTriggerResponse)) {
            throw new AdminClientException("조건을 만족하는 트리거는 존재하지 않습니다", "AC02");
        }
        // 갱신할 스케줄러 주기
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp);
        // Quartz 트리거 찾기
        String triggerName = cronTriggerResponse.getTriggerName();
        String triggerGroupName = cronTriggerResponse.getTriggerGroup();
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(triggerName.replace(TRIGGER_NAME_SUX, "")));
            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail) // QuartzJobBean 이름을 명시,
                    .withIdentity(triggerKey)
                    .withSchedule(cronScheduleBuilder)
                    .build();

            Date newlyDate = scheduler.rescheduleJob(triggerKey, trigger);
            LocalDateTime newlyFirstFireTime = newlyDate
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            log.info("\n=========================================" +
                    "\nOccur Trigger renewal Event" +
                    "\nQuartz Job Name : {} " +
                    "\nTrigger Name : {}" +
                    "\nNewly First Execution Time : {} " +
                    "\nCronExpression : {} " +
                    "\n=========================================", jobDetail.getKey().getName(), triggerName, newlyFirstFireTime, request.getCronExpression());
        } catch (SchedulerException e) {
            throw new AdminClientException("조건을 만족하는 Job이 존재하지 않습니다.", "AC03");
        }
    }

    /** 트리거 중지 **/
    @Transactional
    public void pauseJobFromScheduler(String jobName) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public JobSchedulingResponse readTriggerInformation(String triggerName) {
        JobSchedulingResponse jobSchedulingResponse = quartzExploreMapper.findSchedulingInformation(triggerName);
        if (Objects.isNull(jobSchedulingResponse)) {
            throw new AdminClientException("조건을 만족하는 트리거는 존재하지 않습니다", "AC02");
        }


        reconJobSchedulingResponse(jobSchedulingResponse);
        return jobSchedulingResponse;
    }

    public List<JobSchedulingResponse> readAllTriggerInformation() {
        List<JobSchedulingResponse> jobSchedulingResponses = quartzExploreMapper.findAllSchedulingInformation();

        for (JobSchedulingResponse jobSchedulingResponse : jobSchedulingResponses) {
            reconJobSchedulingResponse(jobSchedulingResponse);
        }
        return jobSchedulingResponses;
    }

    private static void reconJobSchedulingResponse(JobSchedulingResponse jobSchedulingResponse) {
        String cronExpression = jobSchedulingResponse.getCronExpression();

        if (Objects.nonNull(cronExpression) && CronExpression.isValidExpression(cronExpression)) {
            LocalDate fireDate = parse(cronExpression).next(LocalDateTime.now()).toLocalDate();
            LocalTime fireTime = parse(cronExpression).next(LocalDateTime.now()).toLocalTime();
            LocalDateTime nextFireTime = LocalDateTime.of(fireDate, fireTime);
            jobSchedulingResponse.setNextFireTime(nextFireTime);
        }

        // 트리거 상태가 PAUSE 중지 이거나 NULL (트리거가 없을 때)
        String triggerState = jobSchedulingResponse.getTriggerState();
        if (Objects.equals(triggerState, "PAUSED") || Objects.isNull(triggerState)){
            jobSchedulingResponse.setSchedulingUsed(false);
        } else {
            jobSchedulingResponse.setSchedulingUsed(true);
        }
    }

    public List<JobParamResponse> findJobParameterBy(String jobName) {
        JobMetaData jobMetaData = jobMetaDataRepository.fetchByJobName(jobName)
                .orElseThrow(() -> new AdminClientException("존재하지 않는 잡 이름입니다.", "AC05"));

        List<JobParam> jobParams = jobMetaData.getJobParams();
        return jobParams.stream()
                .map(jp -> new JobParamResponse(jp.getParameterKey(), jp.getParamDescription(), jp.getPlaceHolder(), jp.isRequired()))
                .toList();
    }
}
