package com.jxx.groupware.batch.dto.response;

import com.jxx.groupware.batch.dto.request.EnrollJobParam;

import java.time.LocalDateTime;
import java.util.List;

public record EnrollJobResponse(
        Integer jobPk,
        String jobName,
        String jobDescription,
        LocalDateTime enrolledDateTime,
        List<EnrollJobParam> jobParams
) {
}
