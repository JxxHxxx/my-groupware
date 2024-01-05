package com.jxx.vacation.core.domain;

import com.jxx.vacation.core.vacation.domain.entity.VacationStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class MessageQ {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_Q_PK", nullable = false)
    private Long pk;
    @Column(name = "REQUESTER_ID", nullable = false)
    private String requesterId;
    @Column(name = "VACATION_STATUS", nullable = false)

    private VacationStatus vacationStatus;
    @Column(name = "REQUEST_VACATION_DATE", nullable = false)
    private float requestVacationDate;
    @Column(name = "EVENT_TIME", nullable = false)

    private LocalDateTime eventTime;
    @Column(name = "PROCESS_TIME", nullable = true)
    private LocalDateTime processTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "MESSAGE_STATUS", nullable = false)
    private MessageStatus messageStatus;


    @Builder
    public MessageQ(String requesterId, VacationStatus vacationStatus, float requestVacationDate, LocalDateTime processTime) {
        this.requesterId = requesterId;
        this.vacationStatus = vacationStatus;
        this.requestVacationDate = requestVacationDate;
        this.eventTime = LocalDateTime.now();
        this.processTime = processTime;
        this.messageStatus = MessageStatus.SENT;
    }
}
