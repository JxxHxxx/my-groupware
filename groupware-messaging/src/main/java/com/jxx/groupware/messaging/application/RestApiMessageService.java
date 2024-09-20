package com.jxx.groupware.messaging.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxx.groupware.core.message.domain.MessageProcessStatus;
import com.jxx.groupware.core.message.domain.MessageQ;
import com.jxx.groupware.core.message.domain.MessageQResult;
import com.jxx.groupware.core.message.infra.MessageQRepository;
import com.jxx.groupware.core.message.infra.MessageQResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static com.jxx.groupware.core.message.MessageConst.RETRY_HEADER;

@Slf4j
@Service(value = "restApiMessageService")
public class RestApiMessageService implements MessageService<MessageQ> {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MessageQRepository messageQRepository;
    private final MessageQResultRepository messageQResultRepository;

    public RestApiMessageService(MessageQRepository messageQRepository, MessageQResultRepository messageQResultRepository) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.messageQRepository = messageQRepository;
        this.messageQResultRepository = messageQResultRepository;
    }

    @Override
    public void process(Message<MessageQ> message) {
        processMessage(message, null);
    }

    @Override
    public void retry(Message<MessageQ> message) {
        processMessage(message, message.getHeaders().get(RETRY_HEADER, Long.class));
    }

    private void processMessage(Message<MessageQ> message, Long originalMessageQPk) {
        MessageQ messageQ = message.getPayload();

        String baseUrl = String.valueOf(messageQ.getBody().get("baseUrl"));
        String path = String.valueOf(messageQ.getBody().get("path"));
        ;
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
                        // 최초 메시지 큐 PK 할당
                        Long receivedOriginalMessageQPk = Objects.isNull(originalMessageQPk) ? messageQ.getPk() : originalMessageQPk;

                        MessageQResult messageQResult = MessageQResult.builder()
                                .messageProcessStatus(MessageProcessStatus.SUCCESS)
                                .processStartTime(messageQ.getProcessStartTime())
                                .processEndTime(LocalDateTime.now())
                                .messageProcessType(messageQ.getMessageProcessType())
                                .messageDestination(messageQ.getMessageDestination())
                                .eventTime(messageQ.getEventTime())
                                .originalMessagePk(receivedOriginalMessageQPk)
                                .body(messageQ.getBody())
                                .build();

                        messageQRepository.deleteById(messageQ.getPk());
                        messageQResultRepository.save(messageQResult);
                    } else {
                        log.error("통신 오류 {}", statusCode);
                    }
                    // 대상지 연결 실패 시, 예외 처리
                } catch (ResourceAccessException exception) {
                    log.error("통신 실패", exception);
                    // 최초 메시지 큐 PK 할당
                    Long receivedOriginalMessageQPk = Objects.isNull(originalMessageQPk) ? messageQ.getPk() : originalMessageQPk;

                    MessageQResult messageQResult = MessageQResult.builder()
                            .messageProcessStatus(MessageProcessStatus.FAIL)
                            .processStartTime(messageQ.getProcessStartTime())
                            .processEndTime(LocalDateTime.now())
                            .messageProcessType(messageQ.getMessageProcessType())
                            .messageDestination(messageQ.getMessageDestination())
                            .eventTime(messageQ.getEventTime())
                            .originalMessagePk(receivedOriginalMessageQPk)
                            .body(messageQ.getBody())
                            .build();

                    messageQRepository.deleteById(messageQ.getPk());
                    messageQResultRepository.save(messageQResult);
                }
            }
            case "PATCH" -> log.info("PATCH API 처리");
            case "DELETE" -> log.info("DELETE API 처리");
            case "PUT" -> log.info("PUT API 처리");
            default -> throw new RuntimeException();
        }
    }
}
