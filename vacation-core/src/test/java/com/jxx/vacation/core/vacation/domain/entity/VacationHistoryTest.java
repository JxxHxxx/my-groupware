package com.jxx.vacation.core.vacation.domain.entity;


import com.jxx.vacation.core.common.history.HistoryEntityFieldValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class VacationHistoryTest {

    @DisplayName("연관 관계 매핑을 위한 vacationDurations 필드 제외," +
            "DEDUCTED_DEFAULT_VALUE 클래스 필드로 제외," +
            "id 필드 -> 히스토리 앤티티 pk와 분명한 분별을 위해 vacationId 로 변경")
    @Test
    void history_entity_has_required_field() throws NoSuchFieldException {
        HistoryEntityFieldValidator fieldValidator = new HistoryEntityFieldValidator(Vacation.class, VacationHistory.class);
        fieldValidator.validate(List.of("DEDUCTED_DEFAULT_VALUE", "vacationDurations"), Map.of("id", "vacationId"));

    }
}