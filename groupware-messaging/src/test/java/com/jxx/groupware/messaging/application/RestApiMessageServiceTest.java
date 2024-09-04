package com.jxx.groupware.messaging.application;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

class RestApiMessageServiceTest {

    @Test
    void httpMethodCast() {
        HttpMethod httpMethod = HttpMethod.valueOf("GET1");
        System.out.println(httpMethod);
    }
}