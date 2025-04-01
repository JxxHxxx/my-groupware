package com.jxx.groupware.api.messaging.presentation;

import com.jxx.groupware.api.SpitEamClientV2;
import com.jxx.groupware.api.messaging.application.MessageDestinationService;
import com.jxx.groupware.api.messaging.dto.request.DataSourceConnectionRequest;
import com.jxx.groupware.api.messaging.dto.request.MessageQDestinationRequest;
import com.jxx.groupware.api.messaging.dto.response.DataSourceConnectionResponse;
import com.jxx.groupware.api.messaging.dto.response.MessageQDestinationResponse;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.rathontech.common.crypt.RathonCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/api/test-eam")
    public ResponseEntity<?> testEam() throws Exception {
        SpitEamClientV2 spitEamClient = new SpitEamClientV2();
        String[] userAuthList = spitEamClient.getUserDeptByUserID("DBA40", "T210037");
        spitEamClient.close();
        return ResponseEntity.ok(new ResponseResult<>(200, "EAM 시스템 리스트", Arrays.asList(userAuthList)));

    }
    @GetMapping("/api/test-eam/v2")
    public ResponseEntity<?> testEamV2() throws Exception {
        log.info("call v2");
        RathonCrypt rc = new RathonCrypt();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(3000);

        String url = "https://im.daelimcloudtest.com/rtim-manager/api/eam/getUserAllEamMapping";
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        String id = "rathon";
        String token = "fkxhs123";
        String timeStamp = new Date().getTime() + "";
        String serviceId = "DAB40_10";
        String rtUid = "DAEH2500180";
        // 추가정보
        String rtName = "도혜원";

        Map<String, String> vars = new HashMap<>();
        vars.put("ID", id);// 필수
        vars.put("TOKEN", token);// 필수
        vars.put("TIMESTAMP", timeStamp);// 필수

        vars.put("SERVICEID", serviceId);// 필수
        vars.put("RTUID", rtUid);// 필수
        vars.put("RTNAME", rtName);

        // 메세지 보안 적용 헤더 담기전에 꼭 필수로 해줘야한다 안할시 메세지 보안 실패 메세지 보안 하기위해서는 라톤 크립스 써야함
        rc.generateHMac(vars);
        ObjectMapper om = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setConnection("close");
        HttpEntity request = new HttpEntity(om.writeValueAsBytes(vars), headers);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return ResponseEntity.ok(new ResponseResult<>(200, "EAM 시스템 리스트", result));
    }
}
