package com.jxx.vacation.core.company.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "VACATION_MEMBER_HISTORY")
public class MemberHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_HISTORY PK")
    @Comment(value = "사용자 히스토리 테이블 PK")
    private Long id;


    @ManyToOne
    private Member member;

}
