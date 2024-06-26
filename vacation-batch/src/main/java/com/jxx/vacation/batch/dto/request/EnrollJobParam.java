package com.jxx.vacation.batch.dto.request;

public record EnrollJobParam(
        String parameterKey,
        String paramDescription,
        boolean required
) {
}
