package com.jxx.vacation.api.messaging.presentation;

import com.jxx.vacation.api.messaging.application.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessagingApiController {

    private final MessageService messageService;

    @PatchMapping("/message-q-results/{message-q-result-pk}/retry")
    public void requestRetry(@PathVariable("message-q-result-pk") Long messageResultPk) {
        messageService.retry(messageResultPk);
    }
}
