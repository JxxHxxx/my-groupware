package com.jxx.vacation.api.messaging.presentation;

import com.jxx.vacation.api.messaging.application.MessageService;
import com.jxx.vacation.api.messaging.dto.response.MessageQResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageApiController {

    private final MessageService messageService;

    //한 번이라도 실패 이력이 있는 MessageQ 에 대해 조회한다. 실패 이력 조회
    @GetMapping("/message-q-results/fail")
    public ResponseEntity<?> getNotSucceededMessage(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "20") int size,
                                                    @RequestParam(value = "std") String std,
                                                    @RequestParam(value = "edd") String edd) {
        PageImpl<MessageQResultResponse> responses = messageService.findProcessFailMessages(page, size, std, edd);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/message-q-results/{message-q-result-pk}/retry")
    public void requestRetry(@PathVariable("message-q-result-pk") Long messageResultPk) {
        messageService.retry(messageResultPk);
    }
}
