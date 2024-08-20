package com.jxx.groupware.api.work.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkDetailServiceResponse(
        Long workDetailPk,
        String analyzeContent,
        LocalDateTime analyzeCompletedTime,
        String workPlanContent,
        LocalDateTime workPlanCompletedTime,
        LocalDate expectDeadlineDate,
        String receiverId,
        String receiverName,
        LocalDateTime createTime,
        Boolean preReflect,
        String preReflectReason
) {
}
