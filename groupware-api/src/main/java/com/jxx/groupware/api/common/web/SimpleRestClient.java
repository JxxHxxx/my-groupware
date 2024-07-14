package com.jxx.groupware.api.common.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponents;

import java.net.URI;

@Slf4j
public class SimpleRestClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SimpleRestClient() {
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        this.objectMapper = new ObjectMapper();
    }

    public <T> T post(UriComponents uriComponents, Object request, Class<T> responseType) throws JsonProcessingException {
        String stringResponse = null;
        URI uri = uriComponents.toUri();
        String requestServerIp = uri.getHost() + ":" + uri.getPort();

        try {
            stringResponse = restTemplate.postForObject(uri, request, String.class);
        }
        catch (RestClientException exception) {
            RequestUri requestUri = new RequestUri(uri.getHost(), uri.getPort(), uri.getPath());
            // 4xx, 5xx 예외
            if (exception instanceof HttpStatusCodeException httpStatusCodeException) {
                log.warn("서버 {} 으로부터 2xx 응답을 받지 못했습니다.", requestServerIp);
                ResponseResult<String> responseBody = httpStatusCodeException.getResponseBodyAs(ResponseResult.class);
                throw new ServerCommunicationException(
                        responseBody.getStatus(),
                        responseBody.getData(),
                        responseBody.getMessage(),
                        requestUri,
                        exception);
            }

            if (exception instanceof ResourceAccessException resourceAccessException) {
                log.warn("host:{} 와 정상적으로 통신하지 못했습니다.", requestServerIp, exception);
                throw new ServerCommunicationException("서버:" + requestServerIp + "와 연결 실패, 연결 상태를 확인해주세요", requestUri, exception);
            }

            throw new ServerCommunicationException(exception.getMessage(), requestUri, exception);
        }
        return objectMapper.readValue(stringResponse, responseType); // 이 메서드가 리턴하는 값을 타입으로 하고 싶음
    }

    public <T> T patch(UriComponents uriComponents, Object request, Class<T> responseType) throws JsonProcessingException {
        String stringResponse = null;
        URI uri = uriComponents.toUri();
        String requestServerIp = uri.getHost() + ":" + uri.getPort();

        try {
            stringResponse = restTemplate.patchForObject(uri, request, String.class);
        }
        catch (RestClientException exception) {
            RequestUri requestUri = new RequestUri(uri.getHost(), uri.getPort(), uri.getPath());
            // 4xx, 5xx 상태 코드 에러
            if (exception instanceof HttpStatusCodeException httpStatusCodeException) {
                log.warn("서버 {} 으로부터 2xx 응답을 받지 못했습니다.", requestServerIp);
                ResponseResult<String> responseBody = httpStatusCodeException.getResponseBodyAs(ResponseResult.class);
                throw new ServerCommunicationException(
                        responseBody.getStatus(),
                        responseBody.getData(),
                        responseBody.getMessage(),
                        requestUri,
                        exception);
            }

            if (exception instanceof ResourceAccessException resourceAccessException) {
                log.warn("host:{} 와 정상적으로 통신하지 못했습니다.", requestServerIp, exception);
                throw new ServerCommunicationException("서버:" + requestServerIp + "와 연결 실패, 연결 상태를 확인해주세요", requestUri, exception);
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
