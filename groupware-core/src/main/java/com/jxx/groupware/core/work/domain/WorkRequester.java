package com.jxx.groupware.core.work.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Embeddable
@NoArgsConstructor
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
}
