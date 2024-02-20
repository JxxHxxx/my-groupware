package com.jxx.vacation.api.common.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentResponse;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import org.junit.jupiter.api.Test;


class SimpleRestClientTest {

    @Test
    void test() {
        String hello = "hello";
        Object dummy = dummy(hello);

        System.out.println(dummy.getClass());
    }

    public Object dummy(Object o) {
        return o;
    }

    @Test
    void simple() throws JsonProcessingException {
        SimpleRestClient simpleRestClient = new SimpleRestClient();
        ResponseResult<ConfirmDocumentResponse> result = simpleRestClient.get("http://localhost:8000/api/confirm-documents/{confirm-document-pk}",
                ResponseResult.class,
                1000);

        System.out.println("result" + result);
    }

}