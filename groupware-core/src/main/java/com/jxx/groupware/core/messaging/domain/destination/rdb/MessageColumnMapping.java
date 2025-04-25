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
@Table(name = "JXX_MESSAGE_COLUMN_MAPPING", indexes = @Index(name = "SID_CNM_MPT_IDX", columnList = "SERVICE_ID, COLUMN_NAME", unique = true))
public class MessageColumnMapping {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_COLUMN_MAPPING_PK")
    private Long messageColumnMappingPk;
    @JoinColumn(name = "SERVICE_ID", referencedColumnName = "SERVICE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private MessageTableMapping messageTableMapping;

    @Column(name = "COLUMN_TYPE")
    private String columnType;
    @Column(name = "COLUMN_NAME")
    private String columnName;

    @Column(name = "USED", columnDefinition = "TINYINT(1)")
    private boolean used;
    @Column(name = "LAST_MODIFIED_TIME")
    private LocalDateTime lastModifiedTime;

    @Builder
    public MessageColumnMapping(MessageTableMapping messageTableMapping, String columnType, String columnName,
                                boolean used, LocalDateTime lastModifiedTime) {
        this.messageTableMapping = messageTableMapping;
        this.columnType = columnType;
        this.columnName = columnName;
        this.used = used;
        this.lastModifiedTime = lastModifiedTime;
    }

    public String receiveDestinationId() {
        return this.messageTableMapping.getDestinationId();
    }
}
