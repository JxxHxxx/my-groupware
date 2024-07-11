package com.jxx.vacation.batch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_BATCH_JOB_META_DATA")
public class JobMetaData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOB_META_DATA_PK")
    private int pk;
    @Column(name = "JOB_NAME", unique = true)
    @Comment(value = "잡 이름(고유한 값으로 사실상 잡ID)")
    private String jobName;
    @Column(name = "JOB_DESCRIPTION")
    @Comment(value = "잡 설명")
    private String jobDescription;
    @Column(name = "USED")
    @Comment(value = "사용 여부")
    private boolean used;
    @Column(name = "ENROLLED_TIME")
    @Comment(value = "최초 잡 등록 시간")
    private LocalDateTime enrolledTime;
    @Column(name = "EXECUTION_TYPE")
    @Comment(value = "실행 유형")
    private String executionType;
    @Column(name = "EXECUTION_TIME")
    @Comment(value = "실행 시간")
    private LocalTime executionTime;
    @Column(name = "EXECUTION_DURATION")
    @Comment(value = "실행 주기")
    private Integer executionDuration;
    @Column(name = "CRON_EXPRESSION")
    @Comment(value = "실행 주기 크론 표현식")
    private String cronExpression;
    @Column(name = "TRIGGER_NAME")
    @Comment(value = "트리거 이름")
    private String triggerName;
    @Column(name = "TRIGGER_GROUP")
    @Comment(value = "트리거 그룹")
    private String triggerGroup;

    @Comment(value = "논리 FK, 잡 메타테이블 PK")
    @OneToMany(mappedBy = "jobMetaData")
    private List<JobParam> jobParams = new ArrayList<>();

    @Builder
    public JobMetaData(String jobName,
                       String jobDescription,
                       boolean used,
                       LocalDateTime enrolledTime,
                       String executionType,
                       LocalTime executionTime,
                       Integer executionDuration,
                       String cronExpression,
                       String triggerName,
                       String triggerGroup) {
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.used = used;
        this.enrolledTime = enrolledTime;
        this.executionType = executionType;
        this.executionTime = executionTime;
        this.executionDuration = executionDuration;
        this.cronExpression = cronExpression;
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
    }

    // 이거 미완성임 executionTime 잘못됨

    public void updateExecutionInfo(String cronExpression) {
        boolean dailyScheduler = cronExpression.endsWith("* * ?");
        if (dailyScheduler) {
            this.executionType = "daily";
        }
        else {
            this.executionType = "etc";
        }

        this.cronExpression = cronExpression;
        this.executionTime = CronExpression.parse(cronExpression).next(LocalDateTime.now()).toLocalTime();
    }

    public void validateTriggerIdentity(String triggerName, String triggerGroup){
        if (!this.triggerName.equals(triggerName) || !this.triggerGroup.equals(triggerGroup)) {
            throw new AdminClientException("AC02", "트리거 동일성 검증에 실패했습니다.");
        }
    }

    public LocalTime getNextFireTime() {
        return CronExpression.parse(cronExpression).next(LocalDateTime.now()).toLocalTime();
    }
}
