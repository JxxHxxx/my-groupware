package com.jxx.groupware.core.vacation.domain.entity;


import com.jxx.groupware.core.common.history.HistoryEntityFieldValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberLeaveHistoryTest {
    @DisplayName("MemberLeaveHistory 엔티티가 MemberLeave 엔티티의 모든 필드를 가지고 있는지 검증한다." +
            "MemberLeave 엔티티의 pk -> MemberLeaveHistory memberPk 로 매핑된다.")
    @Test
    void validate_memberLeave_history_field() {
        HistoryEntityFieldValidator fieldValidator = new HistoryEntityFieldValidator(MemberLeave.class, MemberLeaveHistory.class);

        assertThatCode(() -> fieldValidator.validate(Map.of("pk", "memberPk")))
                .doesNotThrowAnyException();
    }
}