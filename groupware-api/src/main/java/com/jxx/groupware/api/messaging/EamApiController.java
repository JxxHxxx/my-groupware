package com.jxx.groupware.api.messaging;

import com.jxx.groupware.api.SpitEamClientV2;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.rathontech.common.crypt.RathonCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EamApiController {

    @GetMapping("/api/test-eam")
    public ResponseEntity<?> testEam(@RequestParam(name = "serviceId") String serviceId) throws Exception {
        com.spit.eam.client.SpitEamClientV2 spitEamClientV2 = new com.spit.eam.client.SpitEamClientV2();
        String[] sysTemRoleList = spitEamClientV2.getSystemRoleListBySystemCode(serviceId);
        return ResponseEntity.ok(new ResponseResult<>(200, "EAM 시스템 리스트", Arrays.asList(sysTemRoleList)));

    }
    @GetMapping("/api/test-eam/v2")
    public ResponseEntity<?> testEamV2() throws Exception {
        log.info("call v2");
        RathonCrypt rc = new RathonCrypt();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

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
        HttpEntity request = new HttpEntity(om.writeValueAsBytes(vars), headers);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return ResponseEntity.ok(new ResponseResult<>(200, "EAM 시스템 리스트", result));
    }
}
