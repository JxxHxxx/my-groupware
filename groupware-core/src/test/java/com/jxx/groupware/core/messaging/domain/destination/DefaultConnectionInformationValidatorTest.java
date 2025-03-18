package com.jxx.groupware.core.messaging.domain.destination;


import com.jxx.groupware.core.messaging.domain.destination.dto.ConnectionInformationRequiredResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class DefaultConnectionInformationValidatorTest {

    @Test
    void required_false_case() {
        DefaultConnectionInformationValidator validator = new DefaultConnectionInformationValidator();

        Map<String, Object> connectionInformation = new HashMap<>();
        /** username, driverClassName 누락 **/
        connectionInformation.put("url", "testUrl");
//        connectionInformation.put("username", "sa");
        connectionInformation.put("password", "****");
//        connectionInformation.put("driverClassName", "oracle")

        ConnectionInformationRequiredResponse response = validator.required(ConnectionType.RDB, connectionInformation);
        assertThat(response.meet()).isFalse();
        assertThat(response.notMatchedKeys()).containsOnly("username","driverClassName");
    }

    @Test
    void required_true_case() {
        DefaultConnectionInformationValidator validator = new DefaultConnectionInformationValidator();

        Map<String, Object> connectionInformation = new HashMap<>();
        connectionInformation.put("baseUrl", "testUrl");
        connectionInformation.put("path", "/api/con");
        connectionInformation.put("method", "POST");

        ConnectionInformationRequiredResponse response = validator.required(ConnectionType.REST, connectionInformation);
        assertThat(response.meet()).isTrue();
        assertThat(response.notMatchedKeys()).isEmpty();
    }
}