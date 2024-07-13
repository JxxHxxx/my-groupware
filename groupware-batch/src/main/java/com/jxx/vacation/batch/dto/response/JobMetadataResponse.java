package com.jxx.vacation.batch.dto.response;

import java.util.List;

public record JobMetadataResponse(
        String jobName,
        String jobDescription,
        List<JobParamResponse> jobParams
) {
}
