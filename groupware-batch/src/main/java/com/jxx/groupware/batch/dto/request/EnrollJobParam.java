package com.jxx.groupware.batch.dto.request;

public record EnrollJobParam(
        String parameterKey,
        String paramDescription,
        String placeHolder,
        boolean required
) {
}
