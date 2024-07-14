package com.jxx.groupware.core.vacation.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter(AccessLevel.PROTECTED)
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Leave {
    @Column(name = "TOTAL_LEAVE")
    @Comment(value = "연차 잔여일")
    private Float totalLeave;
    @Column(name = "REMAINING_LEAVE")
    @Comment(value = "연차 잔여일")
    private Float remainingLeave;
}
