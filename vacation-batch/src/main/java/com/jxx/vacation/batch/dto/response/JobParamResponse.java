package com.jxx.vacation.batch.dto.response;

public record JobParamResponse(
        String parameterKey,
        String paramDescription,
        String placeHolder,
        boolean required
) {
}
