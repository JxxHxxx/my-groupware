package com.jxx.groupware.core.message.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "JXX_MESSAGE_Q")
public class MessageQ {

    public static final Long ERROR_ORIGINAL_MESSAGE_PK = Long.MIN_VALUE;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_Q_PK", nullable = false)
    @Comment(value = "메시지Q PK")
    private Long pk;

    @Enumerated(EnumType.STRING)
    @Column(name = "MESSAGE_DESTINATION")
    @Comment(value = "목적지")
    private MessageDestination messageDestination;

    @Type(JsonType.class)
    @Column(name = "BODY", columnDefinition = "json")
    @Comment(value = "메시지 본문")
    private Map<String, Object> body = new HashMap<>();

    @Column(name = "MESSAGE_PROCESS_STATUS", nullable = false)
    @Comment(value = "메시지 처리 상태")
    @Enumerated(EnumType.STRING)
    private MessageProcessStatus messageProcessStatus;

    @Column(name = "MESSAGE_PROCESS_TYPE", nullable = false)
    @Comment(value = "메시지 처리 유형")
    @Enumerated(EnumType.STRING)
    private MessageProcessType messageProcessType;

    @Column(name = "EVENT_TIME", nullable = false)
    @Comment(value = "메시지 생성 시간")
    private LocalDateTime eventTime;

    @Column(name = "PROCESS_START_TIME", nullable = true)
    @Comment(value = "메시지 처리 시간 시간")
    private LocalDateTime processStartTime;
    @Column(name = "RETRY_ID", nullable = true)
    @Comment(value = "재시도 할 MESSAGEQ PK")
    private Long retryId; // 재시도를 위한 키

    @Builder
    public MessageQ(Long retryId,
                    MessageDestination messageDestination,
                    MessageProcessStatus messageProcessStatus,
                    MessageProcessType messageProcessType,
                    Map<String, Object> body) {
        this.retryId = retryId;
        this.messageDestination = messageDestination;
        this.messageProcessStatus = messageProcessStatus;
        this.messageProcessType = messageProcessType;
        this.body = body;
        this.processStartTime = null;
        this.eventTime = LocalDateTime.now();
    }

    public void startProduce() {
        this.processStartTime = LocalDateTime.now();
        this.messageProcessStatus = MessageProcessStatus.PROCESS;
    }
    @Override
    public String toString() {
        return "MessageQ{" +
                "pk=" + pk +
                ", messageDestination=" + messageDestination +
                ", body=" + body +
                ", messageProcessStatus=" + messageProcessStatus +
                ", messageProcessType=" + messageProcessType +
                ", eventTime=" + eventTime +
                ", processStartTime=" + processStartTime +
                ", retryId=" + retryId +
                '}';
    }
}
