package com.jxx.vacation.core.vacation.domain.entity;


import com.jxx.vacation.core.common.history.HistoryEntityFieldValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class VacationHistoryTest {

    @Test
    void test_field() throws NoSuchFieldException {
        HistoryEntityFieldValidator fieldValidator = new HistoryEntityFieldValidator(Vacation.class, VacationHistory.class);
        fieldValidator.validate(List.of("DEDUCTED_DEFAULT_VALUE"), Map.of("id", "vacationId"));

    }
}