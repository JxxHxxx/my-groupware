package com.jxx.vacation.api.vacation.application.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.vacation.api.common.web.SimpleRestClient;
import com.jxx.vacation.api.vacation.dto.request.ConfirmRaiseRequest;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import com.jxx.vacation.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.BiFunction;

public class ConfirmRaiseApiAdapter implements BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse>{
    private static final String CONFIRM_SERVER_HOST = "http://localhost:8000";

    @Override
    public ConfirmDocumentRaiseResponse apply(Vacation vacation, MemberLeave memberLeave) {
        String companyId = memberLeave.receiveCompanyId();
        String confirmDocumentId = ConfirmDocumentIdGenerator.execute(companyId, vacation.getId());
        ConfirmRaiseRequest confirmRaiseRequest = new ConfirmRaiseRequest(companyId, memberLeave.receiveDepartmentId(), memberLeave.getMemberId());

        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(CONFIRM_SERVER_HOST)
                .path("/api/confirm-documents/{confirm-document-id}/raise")
                .buildAndExpand(confirmDocumentId);

        SimpleRestClient simpleRestClient = new SimpleRestClient();

        ResponseResult result = null;
        try {
            result = simpleRestClient.post(uriComponents, confirmRaiseRequest, ResponseResult.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return simpleRestClient.convertTo(result, ConfirmDocumentRaiseResponse.class);
    }
}
