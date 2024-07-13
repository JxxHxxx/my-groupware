package com.jxx.vacation.api.member.application;


import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class UserSession {
   private final String companyId;
   private final String companyName;
   private final String memberId;
   private final String name;
   private final String departmentId;
   private final String departmentName;

    public UserSession(String companyId, String companyName, String memberId, String name, String departmentId, String departmentName) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.memberId = memberId;
        this.name = name;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }
}
