package com.jxx.vacation.api.vacation.application.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.vacation.api.common.web.RequestUri;
import com.jxx.vacation.api.common.web.ServerCommunicationException;
import com.jxx.vacation.api.common.web.SimpleRestClient;
import com.jxx.vacation.api.vacation.dto.request.ConfirmRaiseRequest;
import com.jxx.vacation.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.vacation.api.vacation.dto.response.ResponseResult;
import com.jxx.vacation.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.vacation.core.message.body.vendor.confirm.ConfirmStatus;
import com.jxx.vacation.core.vacation.domain.entity.MemberLeave;
import com.jxx.vacation.core.vacation.domain.entity.Vacation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.function.BiFunction;

import static org.springframework.http.HttpStatus.*;

@Slf4j
public class ConfirmRaiseApiAdapter implements BiFunction<Vacation, MemberLeave, ConfirmDocumentRaiseResponse> {
    private static final String CONFIRM_SERVER_HOST = "http://localhost:8000";

    /**
     * 결재 서버 상신 API
     */
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
        ConfirmDocumentRaiseResponse raiseResponse;
        try {
            result = simpleRestClient.post(uriComponents, confirmRaiseRequest, ResponseResult.class);
            raiseResponse = simpleRestClient.convertTo(result, ConfirmDocumentRaiseResponse.class);
        } catch (IllegalArgumentException exception) {
            log.warn("exception {}", exception.getMessage(), exception);
            if (OK.value() == result.getStatus()) {
                return new ConfirmDocumentRaiseResponse(null, null, ConfirmStatus.RAISE.name());
            }

            URI uri = uriComponents.toUri();
            RequestUri requestUri = new RequestUri(uri.getHost(), uri.getPort(), uri.getPath());
            throw new ServerCommunicationException(INTERNAL_SERVER_ERROR.value(), "알 수 없는 에러 발생", requestUri , exception);
        } catch (JsonProcessingException exception) {
            log.warn("exception {}", result, exception);
            return new ConfirmDocumentRaiseResponse(null, null, ConfirmStatus.RAISE.name());
        }

        return raiseResponse;
    }
}
