package com.jxx.vacation.batch.dto.response;

import com.jxx.vacation.batch.dto.request.EnrollJobParam;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record EnrollJobResponse(
        Integer JobPk,
        String jobName,
        String jobDescription,
        String executeType,
        LocalDateTime enrolledDateTime,
        LocalTime executionTime,
        Integer executionDuration,
        List<EnrollJobParam> jobParams
) {
}
