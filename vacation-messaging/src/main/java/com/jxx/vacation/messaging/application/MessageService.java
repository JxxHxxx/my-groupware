package com.jxx.vacation.messaging.application;

import org.springframework.messaging.Message;

public interface MessageService<M> {

    void process(Message<M> message);

    void retry(Message<M> message);
}
