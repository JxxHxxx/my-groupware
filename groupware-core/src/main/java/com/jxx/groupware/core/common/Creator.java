package com.jxx.groupware.core.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor
public class Creator {

    @Column(name = "CREATOR_ID")
    @Comment("생성자")
    private String creatorId; 
    
    @Column(name = "CREATE_SYSTEM")
    @Comment("생성 시스템")
    private String createSystem;

    @Column(name = "CREATE_TIME")
    @Comment("생성 시간")
    private LocalDateTime createTime;

    public Creator(String creatorId, String createSystem) {
        this.creatorId = creatorId;
        this.createSystem = createSystem;
        this.createTime = LocalDateTime.now();
    }
}
