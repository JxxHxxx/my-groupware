package com.jxx.vacation.batch.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_BATCH_JOB_PARAM")
public class JobParam {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOB_PARAM_PK")
    private int pk;
    @Column(name = "REQUIRED")
    @Comment("필수 값 여부(true:1/false:0)")
    private boolean required;
    @Column(name = "PARAMETER_KEY")
    @Comment("파라미터 키")
    private String parameterKey;
    @Column(name = "PARAM_DESCRIPTION")
    @Comment("파라미터에 대한 설명")
    private String paramDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_META_DATA_PK", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private JobMetaData jobMetaData;
    @Builder
    public JobParam(boolean required, String parameterKey, String paramDescription, JobMetaData jobMetaData) {
        this.required = required;
        this.parameterKey = parameterKey;
        this.paramDescription = paramDescription;
        this.jobMetaData = jobMetaData;
        // 객체상 연관관계 매핑 편의 메서드
        jobMetaData.getJobParams().add(this);
    }
}
