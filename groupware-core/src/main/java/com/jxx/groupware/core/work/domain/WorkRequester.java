package com.jxx.groupware.core.work.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkRequester {

    @Column(name = "REQUESTER_COMPANY_ID")
    @Comment("요청자 소속 회사 ID")
    private String companyId;
    @Column(name = "REQUESTER_ID")
    @Comment("요청자 ID")
    private String id;
    @Column(name = "REQUESTER_NAME")
    @Comment("요청자 이름")
    private String name;

    public WorkRequester(String companyId, String id, String name) {
        this.companyId = companyId;
        this.id = id;
        this.name = name;
    }

    /** companyId, id 로만 검증 **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkRequester that = (WorkRequester) o;
        return Objects.equals(companyId, that.companyId) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, id);
    }
}
