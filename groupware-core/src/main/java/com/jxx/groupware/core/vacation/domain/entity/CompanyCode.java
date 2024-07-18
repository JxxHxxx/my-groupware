package com.jxx.groupware.core.vacation.domain.entity;


import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_COMPANY_CODE")
public class CompanyCode {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPANY_CODE_PK")
    @Comment("고객사 코드 테이블 PK")
    private int companyCodePk;
    @Column(name = "COMPANY_ID", unique = true)
    @Comment("고객사 식별자")
    private String companyId;
    @Column(name = "COMPANY_NAME")
    @Comment("고객사 명")
    private String companyName;
    @Column(name = "USED")
    @Comment("사용 여부")
    private boolean used;
    @Column(name = "CREATED_TIME")
    @Comment("등록 일시")
    private LocalDateTime createdTime;
    @Column(name = "DEKETED_TIME")
    @Comment("해제 일시")
    private LocalDateTime deletedTime;

    @Builder
    public CompanyCode(String companyId, String companyName, boolean used, LocalDateTime createdTime, LocalDateTime deletedTime) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.used = used;
        this.createdTime = createdTime;
        this.deletedTime = deletedTime;
    }
}
