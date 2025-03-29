package com.jxx.groupware.api.messaging.presentation;

import com.jxx.groupware.api.messaging.application.MessageDestinationService;
import com.jxx.groupware.api.messaging.dto.request.MessageQDestinationRequest;
import com.jxx.groupware.api.messaging.dto.response.MessageQDestinationResponse;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageDestinationAdminApiController {

    private final MessageDestinationService messageDestinationService;

    @PostMapping("/admin/message-destination")
    public ResponseEntity<?> createDestination(@RequestBody @Validated MessageQDestinationRequest request) {
        MessageQDestinationResponse response = messageDestinationService.createDestination(request);

        return ResponseEntity.status(201).body(new ResponseResult<>(201, "메시지Q 목적지 생성 완료", response));
    }

    @GetMapping("/admin/message-destination")
    public ResponseEntity<?> getDestination(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @ModelAttribute MessageQDestinationRequest request) {
        PageImpl<MessageQDestinationResponse> responses = messageDestinationService.search(page, size);

        return ResponseEntity.ok(new ResponseResult<>(200, "메시지Q 목적지 조회 완료", responses));
    }
}
