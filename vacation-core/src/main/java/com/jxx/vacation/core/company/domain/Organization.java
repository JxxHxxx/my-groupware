package com.jxx.vacation.core.company.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "VACATION_ORGANIZATION_MASTER")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORGANIZATION_PK")
    @Comment(value = "조직 테이블 PK")
    private Long id;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Comment(value = "부서 활성화 여부")
    private boolean isActive;

    @Column(name = "COMPANY_ID", unique = true, nullable = false)
    @Comment(value = "회사 식별자")
    private String companyId;

    @Column(name = "COMPANY_NAME", nullable = false)
    @Comment(value = "회사 명")
    private String companyName;

    @Column(name = "ORGANIZATION_ID", nullable = false)
    @Comment(value = "부서 식별자")
    private String organizationId;
    @Column(name = "NAME", nullable = false)
    @Comment(value = "부서 명")
    private String name;

}
