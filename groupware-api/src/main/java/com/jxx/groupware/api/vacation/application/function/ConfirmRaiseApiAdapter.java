package com.jxx.groupware.api.vacation.application.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.groupware.api.common.web.RequestUri;
import com.jxx.groupware.api.common.web.ServerCommunicationException;
import com.jxx.groupware.api.common.web.SimpleRestClient;
import com.jxx.groupware.api.vacation.dto.request.ConfirmRaiseOrCancelRequest;
import com.jxx.groupware.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.jxx.groupware.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.groupware.core.message.body.vendor.confirm.ConfirmStatus;
import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.domain.entity.Vacation;
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
        ConfirmRaiseOrCancelRequest confirmRaiseOrCancelRequest = new ConfirmRaiseOrCancelRequest(companyId, memberLeave.receiveDepartmentId(), memberLeave.getMemberId());

        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(CONFIRM_SERVER_HOST)
                .path("/api/confirm-documents/{confirm-document-id}/raise")
                .buildAndExpand(confirmDocumentId);

        SimpleRestClient simpleRestClient = new SimpleRestClient();

        URI uri = uriComponents.toUri();
        RequestUri requestUri = new RequestUri(uri.getHost(), uri.getPort(), uri.getPath());

        ResponseResult result = null;
        ConfirmDocumentRaiseResponse raiseResponse = null;
        try {
            // TODO JsonProcessingException 에러도 SimpleRestClient 에서 처리하는게 깔끔할 듯
            result = simpleRestClient.post(uriComponents, confirmRaiseOrCancelRequest, ResponseResult.class);
            raiseResponse = simpleRestClient.convertTo(result, ConfirmDocumentRaiseResponse.class);
        } catch (ServerCommunicationException exception) {
            if (exception.getStatusCode() == 400 && ConfirmStatus.RAISE.name().equals(exception.getErrorCode()))  {
                /** 결재 서버에서만 반영되고 휴가 서버 반영 안될 경우 처리 **/
                log.info("결재 서버에는 이미 반영되었습니다. 결재 서버와 휴가 서버의 데이터 동기화를 위한 처리를 진행합니다.");
                return new ConfirmDocumentRaiseResponse("", "", ConfirmStatus.RAISE.name());
            }
            throw new ServerCommunicationException(exception.getStatusCode(), exception.getMessage(), requestUri, exception);
        } catch (IllegalArgumentException exception) {
            log.info("exception {}", exception.getMessage(), exception);
            if (OK.value() == result.getStatus()) {
                return new ConfirmDocumentRaiseResponse(null, null, ConfirmStatus.RAISE.name());
            }

            throw new ServerCommunicationException(INTERNAL_SERVER_ERROR.value(), "알 수 없는 에러 발생", requestUri, exception);
        } catch (JsonProcessingException exception) {
            // TODO 파싱 에러 왜 이렇게 처리했는지 파악...
            log.warn("exception {}", result, exception);
            return new ConfirmDocumentRaiseResponse(null, null, ConfirmStatus.RAISE.name());
        }

        return raiseResponse;
    }
}
