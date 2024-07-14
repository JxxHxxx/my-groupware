package com.jxx.groupware.core.message.domain;

import com.jxx.groupware.core.common.history.HistoryEntityFieldValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;

class MessageQResultTest {

    @DisplayName("MessageQResult 엔티티가 MessageQ 엔티티의 모든 필드를 가지고 있는지 검증한다." +
            "MessageQ 엔티티의 pk -> MessageQResult ORIGINAL_MESSAGE_PK 로 매핑된다.")
    @Test
    void validate_memberLeave_history_field() {
        HistoryEntityFieldValidator fieldValidator = new HistoryEntityFieldValidator(
                MessageQ.class, MessageQResult.class);

        List<String> exceptValidFields = List.of("ERROR_ORIGINAL_MESSAGE_PK", "retryId");
        Map<String, String> convertFields = Map.of("pk", "originalMessagePk");
        assertThatCode(() -> fieldValidator.validate(exceptValidFields, convertFields))
                .doesNotThrowAnyException();
    }

}