package com.jxx.groupware.core.messaging.domain.destination.rdb;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_MESSAGE_TABLE_MAPPING", indexes = @Index(name = "IDX_DESTINATION_ID_TABLE_NAME", columnList = "DESTINATION_ID, TABLE_NAME", unique = true))
public class MessageTableMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_TABLE_MAPPING")
    private Long messageTableMapping;
    @Column(name = "DESTINATION_ID")
    private String destinationId;

    @Column(name = "TABLE_NAME")
    private String tableName;
    @Column(name = "USED" , columnDefinition = "TINYINT(1)")
    private boolean used;
    @Column(name = "CREATED_TIME")
    private LocalDateTime createdTime;
    @Column(name = "LAST_MODIFIED_TIME")

    private LocalDateTime lastModifiedTime;

    @Builder
    public MessageTableMapping(String destinationId, String tableName, boolean used, LocalDateTime createdTime, LocalDateTime lastModifiedTime) {
        this.destinationId = destinationId;
        this.tableName = tableName;
        this.used = used;
        this.createdTime = createdTime;
        this.lastModifiedTime = lastModifiedTime;
    }
}
