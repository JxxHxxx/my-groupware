package com.jxx.groupware.messaging.application;

import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import org.springframework.messaging.Message;

public class RdbMessageService implements MessageService<MessageQ>{

    @Override
    public void process(Message<MessageQ> message) {

    }

    @Override
    public void retry(Message<MessageQ> message) {

    }
}
