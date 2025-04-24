package com.jxx.groupware.api.messaging.presentation;

import com.jxx.groupware.api.messaging.application.MessageDestinationService;
import com.jxx.groupware.api.messaging.dto.request.DataSourceConnectionRequest;
import com.jxx.groupware.api.messaging.dto.request.MessageQDestinationRequest;
import com.jxx.groupware.api.messaging.dto.request.MessageTableMappingCreateRequest;
import com.jxx.groupware.api.messaging.dto.response.DataSourceConnectionResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageQDestinationResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageTableMappingResponse;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
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

    @PostMapping("/admin/message-destination/check-connection")
    public ResponseEntity<?> checkConnection(@RequestBody DataSourceConnectionRequest request) {
        DataSourceConnectionResponse response = messageDestinationService.isConnectionActivation(request);

        return ResponseEntity.ok(new ResponseResult<>(200, "메시지Q 목적지 연결 여부", response));
    }

    @PostMapping("/admin/message-destination/{destination-id}/table-mappings")
    public ResponseEntity<?> createTableMapping(@PathVariable(name = "destination-id") String destinationId, @RequestBody @Valid MessageTableMappingCreateRequest request) {
        MessageTableMappingResponse response = messageDestinationService.createMessageTableMapping(destinationId, request);

        return ResponseEntity.status(201).body(new ResponseResult<>(201, "메시지Q 목적지 DB 타입 테이블 매핑 정보 등록", response));
    }

    @PostMapping("/admin/message-destination/{destination-id}/table-mappings/{service-id}/column-mappings")
    public ResponseEntity<?> createColumnMapping() {
        return ResponseEntity.status(201).body(new ResponseResult<>(201, "메시지Q 목적지 DB 타입 컬럼 매핑 정보 등록", null));

    }
}
