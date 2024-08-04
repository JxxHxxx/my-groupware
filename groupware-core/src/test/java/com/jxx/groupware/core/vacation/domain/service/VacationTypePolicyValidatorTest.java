package com.jxx.groupware.core.vacation.domain.service;


import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class VacationTypePolicyValidatorTest {

    @DisplayName("Float -> Long 으로 변환할 경우, 소수점은 내림이 된다.")
    @Test
    void convertFloatToLong() {
        Float val1 = 3.5F;
        long longVal1 = val1.longValue();

        assertThat(longVal1).isEqualTo(3l);
    }
}