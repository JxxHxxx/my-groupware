package com.jxx.vacation.messaging.application;

import com.jxx.vacation.core.message.MessageQ;
import org.springframework.messaging.Message;

import java.util.List;

public interface MessageService<M> {

    void process(Message<M> message);

    void retry(Message<M> message);
}
