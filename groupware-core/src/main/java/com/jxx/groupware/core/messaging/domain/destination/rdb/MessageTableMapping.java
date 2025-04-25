package com.jxx.groupware.core.messaging.domain.destination.rdb;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_MESSAGE_TABLE_MAPPING", indexes = @Index(name = "IDX_SERVICE_ID", columnList = "SERVICE_ID", unique = true))
public class MessageTableMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_TABLE_MAPPING_PK")
    private Long messageTableMapping;

    @Column(name = "SERVICE_ID", nullable = false)
    private String serviceId;
    @Comment("MESSAGE DESTINATION 연결 간접키")
    @Column(name = "DESTINATION_ID")
    private String destinationId;
    @Comment("DML 유형")
    @Column(name = "DML_TYPE")
    @Enumerated(value = EnumType.STRING)
    private DmlType dmlType;
    @Column(name = "TABLE_NAME")
    private String tableName;
    @Column(name = "USED" , columnDefinition = "TINYINT(1)")
    private boolean used;
    @Column(name = "CREATED_TIME")
    private LocalDateTime createdTime;
    @Column(name = "LAST_MODIFIED_TIME")

    private LocalDateTime lastModifiedTime;

    @Builder
    public MessageTableMapping(String serviceId, String destinationId, DmlType dmlType, String tableName, boolean used,
                               LocalDateTime createdTime, LocalDateTime lastModifiedTime) {
        this.serviceId = serviceId;
        this.destinationId = destinationId;
        this.dmlType = dmlType;
        this.tableName = tableName;
        this.used = used;
        this.createdTime = createdTime;
        this.lastModifiedTime = lastModifiedTime;
    }
}
