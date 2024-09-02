package com.jxx.groupware.core.work.domain;

import com.jxx.groupware.core.work.domain.exception.WorkClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class WorkDetailTest {

    @DisplayName("분석 단계 완료하기 위해서는 analyzeContent 은 null 이거나 공백일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void complete_analyze_content(String analyzeContent) {
        WorkDetail workDetail = WorkDetail.builder()
                .analyzeContent(null)
                .createTime(LocalDateTime.now())
                .build();

        assertThatThrownBy(() -> workDetail.completeAnalyzeContent(analyzeContent))
                .isInstanceOf(WorkClientException.class);
    }
}