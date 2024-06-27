package com.jxx.vacation.batch.dto.request;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record EnrollJobForm(
        String jobName,
        String jobDescription,
        boolean used,
        String executeType,
        LocalTime executionTime,
        Integer executionDuration,
        List<EnrollJobParam> jobParams
) {
}
