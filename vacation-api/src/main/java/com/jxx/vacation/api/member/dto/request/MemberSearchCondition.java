package com.jxx.vacation.api.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSearchCondition {
    private String companyId;
    private String departmentId;
    private String memberId;
    private String memberName;
    private boolean memberActive;
    private boolean orgActive;

    public void changeOnlyActive() {
        this.memberActive = true;
        this.orgActive = true;
    }
    public void changeCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
