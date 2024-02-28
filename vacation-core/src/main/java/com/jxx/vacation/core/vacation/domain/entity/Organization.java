package com.jxx.vacation.core.vacation.domain.entity;


import com.jxx.vacation.core.vacation.domain.exeception.InactiveException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_ORGANIZATION_MASTER", indexes = @Index(name = "IDX_COMPANY_ORG", columnList = "COMPANY_ID, DEPARTMENT_ID"))
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORGANIZATION_PK")
    @Comment(value = "조직 테이블 PK")
    private Long id;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Comment(value = "부서 활성화 여부")
    private boolean isActive;

    @Column(name = "COMPANY_ID", nullable = false)
    @Comment(value = "회사 식별자")
    private String companyId;

    @Column(name = "COMPANY_NAME", nullable = false)
    @Comment(value = "회사 명")
    private String companyName;

    @Column(name = "DEPARTMENT_ID", nullable = false)
    @Comment(value = "부서 식별자")
    private String departmentId;

    @Column(name = "DEPARTMENT_NAME", nullable = false)
    @Comment(value = "부서 명")
    private String departmentName;

    @Column(name = "PARENET_DEPARTMENT_ID", nullable = false)
    @Comment(value = "상위 부서 식별자")
    private String parentDepartmentId;

    @Column(name = "PARENET_DEPARTMENT_NAME", nullable = false)
    @Comment(value = "상위 부서 명")
    private String parentDepartmentName;

    @Builder
    public Organization(String companyId, String companyName, String departmentId, String departmentName) {
        this.isActive = true;
        this.companyId = companyId;
        this.companyName = companyName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }
    @Builder
    public Organization(String companyId, String companyName, String departmentId, String departmentName,
                        String parentDepartmentId, String parentDepartmentName) {
        this.isActive = true;
        this.companyId = companyId;
        this.companyName = companyName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.parentDepartmentId = parentDepartmentId;
        this.parentDepartmentName = parentDepartmentName;
    }

    public void checkActive() throws InactiveException {
        if (!isActive) {
            throw new InactiveException("활성화 되어있지 않은 조직입니다.");
        }
    }

}
