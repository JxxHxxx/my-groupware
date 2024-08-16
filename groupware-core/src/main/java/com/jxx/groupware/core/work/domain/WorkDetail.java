package com.jxx.groupware.core.work.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_WORK_DETAIL_MASTER")
public class WorkDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workDetailPk;
    @Comment("작업 분석 내용")
    private String analyzeContent;
    @Comment("작업 분석 완료 시간")
    private LocalDateTime analyzeCompleteTime;

    @Comment("작업 계획 내용")
    private String workPlanContent;
    @Comment("작업 계획 완료 시간")
    private LocalDateTime workPlanCompleteTime;

    @Comment("작업 마감 예상일")
    private LocalDate expectDeadLineDate;

    @Comment("접수자 ID")
    private String receiverId;
    @Comment("접수자 이름")
    private String receiverName;
    @Comment("생성 시간")
    private LocalDateTime createTime;
    @Comment("선처리 여부")
    private Boolean preReflect;
    @Comment("선처리 사유")
    private String preReflectReason;

    @Builder
    public WorkDetail(String analyzeContent, LocalDateTime analyzeCompleteTime, String workPlanContent,
                      LocalDateTime workPlanCompleteTime, LocalDate expectDeadLineDate, String receiverId,
                      String receiverName, LocalDateTime createTime, Boolean preReflect, String preReflectReason) {
        this.analyzeContent = analyzeContent;
        this.analyzeCompleteTime = analyzeCompleteTime;
        this.workPlanContent = workPlanContent;
        this.workPlanCompleteTime = workPlanCompleteTime;
        this.expectDeadLineDate = expectDeadLineDate;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.createTime = createTime;
        this.preReflect = preReflect;
        this.preReflectReason = preReflectReason;
    }
}
