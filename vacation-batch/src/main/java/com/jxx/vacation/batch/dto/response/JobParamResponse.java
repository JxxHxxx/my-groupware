package com.jxx.vacation.batch.dto.response;

public record JobParamResponse(
        String parameterKey,
        String paramDescription,
        boolean required
) {
}
