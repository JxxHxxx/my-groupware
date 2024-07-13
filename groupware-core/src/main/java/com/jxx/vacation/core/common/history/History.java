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
    @Comment("발행자")
    private String executor;

    @Column(name = "EXECUTE_TIME")
    @Comment("발행 시간")
    private LocalDateTime executeTime;

    @Column(name = "TASK_TYPE")
    @Comment("작업 유형(I:삽입, U:수정, D:삭제)")
    @Enumerated(value = EnumType.STRING)
    private TaskType taskType;

    protected History(String executor, LocalDateTime executeTime, TaskType taskType) {
        this.executor = executor;
        this.executeTime = executeTime;
        this.taskType = taskType;
    }

    public static History insert(String executor) {
        return new History(executor, LocalDateTime.now(), TaskType.I);
    }

    public static History update(String executor) {
        return new History(executor, LocalDateTime.now(), TaskType.U);
    }

    public static History delete(String executor) {
        return new History(executor, LocalDateTime.now(), TaskType.D);
    }
}
