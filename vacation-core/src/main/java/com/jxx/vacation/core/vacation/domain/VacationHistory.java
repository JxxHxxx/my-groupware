package com.jxx.vacation.core.vacation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_VACATION_HISTORY")
public class VacationHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VACATION_HISTORY_ID")
    @Comment(value = "연차 히스토리 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VACATION_ID", referencedColumnName="ANNUAL_LEAVE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vacation vacation;

    public VacationHistory(Vacation vacation) {
        this.vacation = vacation;
    }
}
