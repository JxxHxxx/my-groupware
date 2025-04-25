package com.jxx.groupware.core.messaging.domain.destination;

import com.jxx.groupware.core.messaging.domain.destination.dto.ConnectionInformationRequiredResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Message Destination connectionType 별 필수 값 존재 여부 검증 클래스 **/
@Slf4j
public class DefaultConnectionInformationValidator implements ConnectionInformationValidator {

    public static final List<String> REQUIRED_RDB_KEYS = List.of("url", "username", "password", "driverClassName");
    public static final List<String> REQUIRED_REST_KEYS = List.of("baseUrl", "path", "method");

    @Override
    public ConnectionInformationRequiredResponse required(ConnectionType conType, Map<String, Object> conInfo) {
        // conInfo 에 필수 키가 존재하는지 검증
        if (Objects.isNull(conType)) {
            throw new RuntimeException("conType is null");
        }
        switch (conType) {
            case RDB -> {
                log.debug("RDB connectionType required key list : {}", REQUIRED_RDB_KEYS);
                List<String> notMatchedKeys = REQUIRED_RDB_KEYS.stream()
                        .filter(key -> !conInfo.containsKey(key))
                        .toList();
                // notMatchedKeys.isEmpty() : 매치되지 않은 키 리스트가 비어있을 경우, 정상 케이스기 때문에 meet 필드에 true 를 넣어준다.
                return new ConnectionInformationRequiredResponse(notMatchedKeys.isEmpty(), conType, notMatchedKeys);
            }
            case REST -> {
                log.debug("REST connectionType required key list {}", REQUIRED_REST_KEYS);
                List<String> notMatchedKeys = REQUIRED_REST_KEYS.stream()
                        .filter(key -> !conInfo.containsKey(key))
                        .toList();
                return new ConnectionInformationRequiredResponse(notMatchedKeys.isEmpty(), conType, notMatchedKeys);
            }
            default -> {
                log.error("conType {} something wrong", conType);
                throw new RuntimeException();
            }
        }
    }
}
