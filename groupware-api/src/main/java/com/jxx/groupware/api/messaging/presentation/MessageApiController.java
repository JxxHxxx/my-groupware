package com.jxx.groupware.api.messaging.presentation;

import com.jxx.groupware.api.messaging.application.MessageService;
import com.jxx.groupware.api.messaging.dto.request.MessageQResultSearchCondition;
import com.jxx.groupware.api.messaging.dto.request.MessagePagingSearchCond;
import com.jxx.groupware.api.messaging.dto.response.MessageQResultResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageQResultResponseV2;
import com.jxx.groupware.api.messaging.query.MessageQResultMapper;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
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
    private final MessageQResultMapper messageQResultMapper;

    //한 번이라도 실패 이력이 있는 MessageQ 에 대해 조회한다. 실패 이력 조회
    @GetMapping("/test/message-q-results/fail")
    public ResponseEntity<?> getFailMessage(@ModelAttribute MessagePagingSearchCond searchCond) {
        PageImpl<MessageQResultResponse> responses = messageService.findProcessFailMessages(
                searchCond.getPage(), searchCond.getSize(), searchCond.getStartDate(), searchCond.getEndDate());
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/message-q-results/{message-q-result-pk}/retry")
    public ResponseEntity<?> requestRetry(@PathVariable("message-q-result-pk") Long messageResultPk) {
        messageService.retry(messageResultPk);
        return ResponseEntity.ok(new ResponseResult<>(200, "재동기 요청 완료", null));
    }

    @GetMapping("/test/message-q-results")
    public ResponseEntity<?> findMessageQResult(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "20") int size,
                                                @ModelAttribute MessageQResultSearchCondition searchCondition) {
        PageImpl<MessageQResultResponseV2> responses = messageService.findSuccessMessageQResult(searchCondition, page, size);
        return ResponseEntity.ok(responses);
    }


}
