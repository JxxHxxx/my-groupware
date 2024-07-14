package com.jxx.groupware.api.member.dto.request;

import java.util.Objects;

public record AuthenticationRequest(
        String memberId,
        String companyId,
        String departmentId,
        String name,
        String companyName,
        String departmentName
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationRequest that = (AuthenticationRequest) o;
        return Objects.equals(memberId, that.memberId) && Objects.equals(companyId, that.companyId) && Objects.equals(departmentId, that.departmentId) && Objects.equals(name, that.name) && Objects.equals(companyName, that.companyName) && Objects.equals(departmentName, that.departmentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, companyId, departmentId, name, companyName, departmentName);
    }
}
