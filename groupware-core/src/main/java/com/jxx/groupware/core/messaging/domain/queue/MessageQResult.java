package com.jxx.groupware.core.messaging.domain.queue;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 1. TimeStamp 타입으로 처리 시간 필드 추가를 고려해볼만 함, 실패 이력 조회 시 성능을 위해
 2. MESSAGE_DESTINATION 의 종류가 많아지면 PROCESS_END_TIME, MESSAGE_DESTINATION 멀티 인덱스 고려해도 될듯?
 **/
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_MESSAGE_Q_RESULT",
        indexes = {
                @Index(name = "IDX_O_MESSAGE_PK", columnList = "ORIGINAL_MESSAGE_PK", unique = false),
                @Index(name = "IDX_PCS_END_TIME", columnList = "PROCESS_END_TIME", unique = false)
        })
@ToString
public class MessageQResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_Q_RESULT_PK", nullable = false)
    @Comment(value = "메시지Q 결과 PK")
    private Long pk;

    @Column(name = "ORIGINAL_MESSAGE_PK", nullable = false)
    @Comment(value = "메시지Q PK")
    private Long originalMessagePk;

    @Column(name = "MESSAGE_DESTINATION")
    @Comment(value = "목적지")
    @Enumerated(EnumType.STRING)
    private MessageDestination messageDestination;

    @Column(name = "MESSAGE_PROCESS_TYPE", nullable = false)
    @Comment(value = "메시지 처리 유형")
    @Enumerated(EnumType.STRING)
    private MessageProcessType messageProcessType;

    @Type(JsonType.class)
    @Column(name = "BODY", columnDefinition = "json")
    @Comment(value = "메시지 본문")
    private Map<String, Object> body = new HashMap<>();

    @Column(name = "MESSAGE_PROCESS_STATUS", nullable = false)
    @Comment(value = "메시지 처리 상태")
    @Enumerated(EnumType.STRING)
    private MessageProcessStatus messageProcessStatus;

    @Column(name = "EVENT_TIME", nullable = false)
    @Comment(value = "메시지 생성 시간")
    private LocalDateTime eventTime;

    @Column(name = "PROCESS_START_TIME", nullable = true)
    @Comment(value = "메시지 처리 시작 시간")
    private LocalDateTime processStartTime;

    @Column(name = "PROCESS_END_TIME", nullable = true)
    @Comment(value = "메시지 처리 종료 시간")
    private LocalDateTime processEndTime;

    @Builder
    public MessageQResult(Long originalMessagePk,
                          MessageDestination messageDestination,
                          Map<String, Object> body,
                          MessageProcessStatus messageProcessStatus,
                          MessageProcessType messageProcessType,
                          LocalDateTime eventTime,
                          LocalDateTime processStartTime,
                          LocalDateTime processEndTime) {
        this.originalMessagePk = originalMessagePk;
        this.messageDestination = messageDestination;
        this.body = body;
        this.messageProcessStatus = messageProcessStatus;
        this.messageProcessType = messageProcessType;
        this.eventTime = eventTime;
        this.processStartTime = processStartTime;
        this.processEndTime = processEndTime;
    }

    public boolean isFail() {
        return MessageProcessStatus.FAIL.equals(this.messageProcessStatus);
    }
}
