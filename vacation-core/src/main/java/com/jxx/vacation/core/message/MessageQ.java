package com.jxx.vacation.core.message;

import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "JXX_MESSAGE_Q")
@ToString
public class MessageQ {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_Q_PK", nullable = false)
    @Comment(value = "메시지Q PK")
    private Long pk;

    @Enumerated(EnumType.STRING)
    @Column(name = "MESSAGE_DESTINATION")
    @Comment(value = "목적지")
    private MessageDestination messageDestination;

    @Type(JsonType.class)
    @Column(name = "PAYLOAD", columnDefinition = "json")
    @Comment(value = "메시지 본문")
    private Map<String, Object> payload = new HashMap<>();

    @Column(name = "EVENT_TIME", nullable = false)
    @Comment(value = "메시지 생성 시간")
    private LocalDateTime eventTime;

    @Column(name = "PROCESS_TIME", nullable = true)
    @Comment(value = "메시지 처리 시간")
    private LocalDateTime processTime;

    @Builder
    public MessageQ(MessageDestination messageDestination, Map<String, Object> payload) {
        this.messageDestination = messageDestination;
        this.payload = payload;
        this.processTime = null;
        this.eventTime = LocalDateTime.now();
    }
}
