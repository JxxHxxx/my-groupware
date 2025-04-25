package com.jxx.groupware.core.messaging.domain.queue;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.jxx.groupware.core.messaging.domain.queue.MessageProcessStatus.*;

class MessageProcessStatusTest {

    @Test
    void isUnprocessable() {
        Assertions.assertThat(UNPROCESSABLE.isUnProcessable()).isTrue();
    }

}