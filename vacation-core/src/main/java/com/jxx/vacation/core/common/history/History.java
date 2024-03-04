package com.jxx.vacation.core.common.history;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor
public class History {

    @Column(name = "EXECUTOR")
    @Comment("실행자")
    private String executor;

    @Column(name = "EXECUTE_TIME")
    @Comment("실행 시간")
    private LocalDateTime executeTime;

    @Column(name = "TASK_TYPE")
    @Comment("작업 유형(I:삽입, U:수정, D:삭제)")
    @Enumerated(value = EnumType.STRING)
    private TaskType taskType;

    public History(String executor, TaskType taskType) {
        this.executor = executor;
        this.executeTime = LocalDateTime.now();
        this.taskType = taskType;
    }
}
