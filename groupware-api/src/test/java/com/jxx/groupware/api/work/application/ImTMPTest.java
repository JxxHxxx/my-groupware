package com.jxx.groupware.api.work.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathontech.common.crypt.RathonCrypt;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.TimeValue;
import org.codehaus.jackson.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Slf4j

public class ImTMPTest {

    @BeforeEach
    void before_each() {

    }
    @Test
    void test() throws IOException, InterruptedException {
        RathonCrypt rc = new RathonCrypt();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(3000);

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        String url = "https://im.daelimcloudtest.com/rtim-manager/api/eam/getUserAllEamMapping";
//        String url = "https://im.daelimcloud.com/rtim-manager/api/eam/getUserAllEamMapping";

        // 필수입력
        String id = "rathon";
        String token = "fkxhs123";
        String timeStamp = new Date().getTime() + "";
        String serviceId = "DAB40_10";
        String rtUid = "DAEH2500180";
        // 추가정보
        String rtName = "도혜원";

        Map<String, String> vars = new HashMap<String, String>();
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
        log.info("{}", result.getBody());

        ResponseEntity<String> result2 = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        log.info("{}", result2.getBody());
    }

    @DisplayName("통합 권한 신청 API 반복 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"DAE240039", "DAE230272", "DAE230277", "DAE230282", "DAE230285", "DAE240001", "DAE240005",
            "DAE240006", "DAE240013", "DAE240014", "DAEHQ3245", "DAEHQ3246", "DAEHQ3247", "DAEHQ3248", "DAEHQ3249", "DAEHQ3250", "DAEHQ3251", "DAEHQ3252"})
    void applyAddEamAuth(String rtoucode) throws JsonProcessingException {
        RathonCrypt rc = new RathonCrypt();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        String url = "https://im.daelimcloudtest.com/rtim-manager/api/eam/applyAddEamAuth";

        Map<String, String> vars = new HashMap<String, String>();
        vars.put("ID", "rathon");// 필수
        vars.put("TOKEN", "fkxhs123");// 필수
        vars.put("TIMESTAMP", new Date().getTime() + "");// 필수

        vars.put("RTUID", "DAET200014");// 필수
        vars.put("SERVICEID_AUTHID", "DCJ:200:"+ rtoucode +":20250328:20260327"); // 서비스 ID:권한 ID:부서코드:시작일자:종료일자
        // 추가정보
        vars.put("WORK_MSG", "신청합니다."); // 요청메세지

        // 메세지 보안 적용 헤더 담기전에 꼭 필수로 해줘야한다 안할시 메세지 보안 실패 메세지 보안 하기위해서는 라톤 크립스 써야함
        rc.generateHMac(vars);
        ObjectMapper om = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity request = new HttpEntity(om.writeValueAsBytes(vars), headers);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        log.info("{}", result.getBody());
    }

    @DisplayName("사용자 신청 테스트")
    @Test
    void userListAddTest() throws JsonProcessingException {
        RathonCrypt rc = new RathonCrypt();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        String url = "https://im.daelimcloudtest.com/rtim-manager/api/user/addUserList";

        List<Map<String, String>> requestBody = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String userIdx = "IM0000" + i;
            Map<String, String> params = new HashMap<>();
            params.put("ID", "rathon");// 필수
            params.put("TOKEN", "fkxhs123");// 필수
            params.put("TIMESTAMP", new Date().getTime() + "");// 필수

            params.put("SYSTEMID", "manager");// 필수
            params.put("RTUID", "DCP" + userIdx);// 필수
            params.put("RTEMPNO", userIdx);
            params.put("RTNAME", userIdx);
            params.put("RTEMAIL", userIdx + "@DAELIMTEST.CO.KR");
            params.put("RTOUCODE", "DCP00003520");
            params.put("RTOU", "Platform사업팀");
            params.put("RTMOBILE","000-0000-0000");
            params.put("RTCUSTOMER01", "DCP");
            params.put("RTCUSTOMER02", "㈜대림");
            params.put("RTCUSTOMER03", "2000");
            params.put("RTCUSTOMER04", "㈜대림");

            rc.generateHMac(params);
            requestBody.add(params);
        }


        ObjectMapper om = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity request = new HttpEntity(om.writeValueAsBytes(requestBody), headers);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        log.info("{}", result.getBody());
    }
}
