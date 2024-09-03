package com.jxx.groupware.api.work.listener;


import com.jxx.groupware.core.message.infra.MessageQRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkEventListener {

    private final MessageQRepository messageQRepository;
}
