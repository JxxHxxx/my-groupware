package com.jxx.groupware.core.messaging.domain.destination;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_MESSAGE_Q_DESTINATION")
public class MessageQDestination {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_Q_DESTINATION_PK")
    @Comment("메시지Q 목적지 엔티티 PK")
    private Long messageQDestinationPk;
    @Column(name = "CONNECTION_TYPE")
    @Enumerated(EnumType.STRING)
    @Comment(value = "연결 유형")
    private ConnectionType connectionType;
    @Type(JsonType.class)
    @Column(name = "CONNECTION_INFORMATION", columnDefinition = "json")
    @Comment(value = "연결 정보")
    private Map<String, Object> connectionInformation = new HashMap<>();
    @Column(name = "DESTINATION_ID", unique = true)
    @Comment(value = "목적지 ID")
    private String destinationId;
    @Column(name = "DESTINATION_NAME")
    @Comment(value = "목적지 명")
    private String destinationName;
    @Column(name = "USED", columnDefinition= "TINYINT(1)")
    @Comment(value = "사용 여부")
    private Boolean used;
    @Column(name = "OFF_DATE_TIME")
    @Comment(value = "미사용 처리된 시간")
    private LocalDateTime offDateTime;
    @Column(name = "CREATE_DATE_TIME")
    @Comment(value = "레코드 생성일자")
    private LocalDateTime createDateTime;

    @Builder
    public MessageQDestination(ConnectionType connectionType, Map<String, Object> connectionInformation,
                               String destinationId, String destinationName, Boolean used, LocalDateTime offDateTime,
                               LocalDateTime createDateTime) {
        this.connectionType = connectionType;
        this.connectionInformation = connectionInformation;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.used = used;
        this.offDateTime = offDateTime;
        this.createDateTime = createDateTime;
    }
}
