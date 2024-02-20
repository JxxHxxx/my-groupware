package com.jxx.vacation.api.common.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import org.springframework.web.client.RestTemplate;

public class SimpleRestClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SimpleRestClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public <T> T post(String url, Object request, Class<T> responseType, Object... uriVariable) throws JsonProcessingException {
        String stringResponse = restTemplate.postForObject(url, request, String.class, uriVariable);

        return objectMapper.readValue(stringResponse, responseType); // 이 메서드가 리턴하는 값을 타입으로 하고 싶음
    }

    public <T> T get(String url, Class<T> responseType,  Object... uriVariable) throws JsonProcessingException {
        String stringResponse = restTemplate.getForObject(url, String.class, uriVariable);

        return objectMapper.readValue(stringResponse, responseType); // 이 메서드가 리턴하는 값을 타입으로 하고 싶음
    }

    public <T> T convertTo(Object from, Class<T> convertType) {
        ObjectMapper ignoreFailFieldObjectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        return ignoreFailFieldObjectMapper.convertValue(from, convertType);
    }

    public <RESULT extends ResponseResult<T> ,T> T convertTo(RESULT responseResult, Class<T> convertType) {
        return objectMapper.convertValue(responseResult.getResponse(), convertType);
    }
}
