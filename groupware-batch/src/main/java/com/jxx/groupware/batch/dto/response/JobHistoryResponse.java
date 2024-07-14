package com.jxx.groupware.batch.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class JobHistoryResponse {
    private final Integer jobExecutionId;
    private final Integer jobInstanceId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String jobName;
    private final String status;
    private final String exitCode;
    private final String exitMessage;


}
