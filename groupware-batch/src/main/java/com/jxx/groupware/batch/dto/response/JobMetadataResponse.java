package com.jxx.groupware.batch.dto.response;

import java.util.List;

public record JobMetadataResponse(
        String jobName,
        String jobDescription,
        List<JobParamResponse> jobParams
) {
}
