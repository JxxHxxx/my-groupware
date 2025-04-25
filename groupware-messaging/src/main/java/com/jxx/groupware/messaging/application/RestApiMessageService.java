package com.jxx.groupware.messaging.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.groupware.core.messaging.domain.queue.MessageQ;
import com.jxx.groupware.core.messaging.infra.MessageQRepository;
import com.jxx.groupware.core.messaging.infra.MessageQResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@Slf4j
@Service(value = "restApiMessageService")
public class RestApiMessageService extends AbstractMessageService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public RestApiMessageService(MessageQRepository messageQRepository, MessageQResultRepository messageQResultRepository) {
        super(messageQRepository, messageQResultRepository);
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void processMessage(MessageQ messageQ) {
        String baseUrl = String.valueOf(messageQ.getBody().get("baseUrl"));
        String path = String.valueOf(messageQ.getBody().get("path"));

        Map<String, Object> requestBody = objectMapper.convertValue(messageQ.getBody().get("requestBody"), Map.class);

        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path(path)
                .build();

        String httpMethod = String.valueOf(messageQ.getBody().get("method"));

        MultiValueMap<String, String> headerMap = new HttpHeaders();
        headerMap.add("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> httpRequestEntity = new HttpEntity<>(requestBody, headerMap);
        switch (httpMethod) {
            case "POST" -> {
                try {
                    ResponseEntity<String> response = restTemplate.postForEntity(uriComponents.toUri(), httpRequestEntity, String.class);
                    HttpStatusCode statusCode = response.getStatusCode();
                    if (statusCode.is2xxSuccessful()) {
                        log.info("통신 성공 {}", statusCode);

                    } else {
                        log.error("통신 오류 {}", statusCode);
                        throw new RuntimeException("statusCode is not 2xx, response statusCode:" + statusCode);
                    }
                    // 대상지 연결 실패 시, 예외 처리
                } catch (ResourceAccessException exception) {
                    log.error("통신 실패", exception);
                    throw new RuntimeException(exception);
                }
            }
            case "PATCH" -> log.info("PATCH API 처리");
            case "DELETE" -> log.info("DELETE API 처리");
            case "PUT" -> log.info("PUT API 처리");
            default -> throw new RuntimeException();
        }
    }
}
