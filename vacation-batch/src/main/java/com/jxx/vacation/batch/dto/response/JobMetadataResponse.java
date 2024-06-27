package com.jxx.vacation.batch.dto.response;

import java.time.LocalTime;
import java.util.List;

public record JobMetadataResponse(
        String jobName,
        String jobDescription,
        boolean used,
        LocalTime executionTime,
        List<JobParamResponse> jobParams
) {
}
