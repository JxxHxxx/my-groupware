package com.jxx.vacation.api.common.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponents;

import java.net.URI;

@Slf4j
public class SimpleRestClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SimpleRestClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public <T> T post(UriComponents uriComponents, Object request, Class<T> responseType) throws JsonProcessingException {
        String stringResponse = null;
        URI uri = uriComponents.toUri();

        try {
            stringResponse = restTemplate.postForObject(uri, request, String.class);
        } catch (RestClientException exception) {
            log.warn("exception occur to {}", uri.getHost(), exception);
            RequestUri requestUri = new RequestUri(uri.getHost(), uri.getPort(), uri.getPath());
            // 4xx, 5xx 예외
            if (exception instanceof HttpStatusCodeException httpStatusCodeException) {
                ResponseResult<String> responseBody = httpStatusCodeException.getResponseBodyAs(ResponseResult.class);
                throw new ServerCommunicationException(responseBody.getStatus(), responseBody.getMessage(), requestUri, exception);
            }

            if (exception instanceof ResourceAccessException resourceAccessException) {
                throw new ServerCommunicationException(resourceAccessException.getMessage(), requestUri, exception);
            }

            throw new ServerCommunicationException(exception.getMessage(), requestUri, exception);
        }
        return objectMapper.readValue(stringResponse, responseType); // 이 메서드가 리턴하는 값을 타입으로 하고 싶음
    }

    public <T> T get(String url, Class<T> responseType, Object... uriVariable) throws JsonProcessingException {
        String stringResponse = restTemplate.getForObject(url, String.class, uriVariable);

        return objectMapper.readValue(stringResponse, responseType); // 이 메서드가 리턴하는 값을 타입으로 하고 싶음
    }

    public <T> T convertTo(Object from, Class<T> convertType) {
        ObjectMapper ignoreFailFieldObjectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        return ignoreFailFieldObjectMapper.convertValue(from, convertType);
    }

    public <RESULT extends ResponseResult<T> ,T> T convertTo(RESULT responseResult, Class<T> convertType) {
        return objectMapper.convertValue(responseResult.getData(), convertType);
    }
}
