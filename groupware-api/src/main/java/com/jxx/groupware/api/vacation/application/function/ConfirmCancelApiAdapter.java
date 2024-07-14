package com.jxx.groupware.api.vacation.application.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jxx.groupware.api.common.web.RequestUri;
import com.jxx.groupware.api.common.web.ServerCommunicationException;
import com.jxx.groupware.api.common.web.SimpleRestClient;
import com.jxx.groupware.api.vacation.dto.request.ConfirmRaiseOrCancelRequest;
import com.jxx.groupware.api.vacation.dto.response.ConfirmDocumentCancelResponse;
import com.jxx.groupware.api.vacation.dto.response.ConfirmDocumentRaiseResponse;
import com.jxx.groupware.api.vacation.dto.response.ResponseResult;
import com.jxx.groupware.core.common.generator.ConfirmDocumentIdGenerator;
import com.jxx.groupware.core.message.body.vendor.confirm.ConfirmStatus;
import com.jxx.groupware.core.vacation.domain.entity.MemberLeave;
import com.jxx.groupware.core.vacation.domain.entity.Vacation;
import com.jxx.groupware.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.function.BiFunction;

@Slf4j
public class ConfirmCancelApiAdapter implements BiFunction<Vacation, MemberLeave, ConfirmDocumentCancelResponse> {
    private static final String CONFIRM_SERVER_HOST = "http://localhost:8000";

    @Override
    public ConfirmDocumentCancelResponse apply(Vacation vacation, MemberLeave memberLeave) {
        String companyId = memberLeave.receiveCompanyId();
        String departmentId = memberLeave.receiveDepartmentId();
        String requesterId = memberLeave.getMemberId();
        String confirmDocumentId = ConfirmDocumentIdGenerator.execute(companyId, vacation.getId());

        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(CONFIRM_SERVER_HOST)
                .path("/api/confirm-documents/{confirm-document-id}/cancel")
                .buildAndExpand(confirmDocumentId);

        SimpleRestClient simpleRestClient = new SimpleRestClient();

        ConfirmDocumentCancelResponse response;
        try {
            ConfirmRaiseOrCancelRequest requestBody = new ConfirmRaiseOrCancelRequest(companyId, departmentId, requesterId);
            // 다른 시스템으로 부터 받은 응답
            ResponseResult result = simpleRestClient.patch(uriComponents, requestBody, ResponseResult.class);
            // 해당 응답을 알맞게 변형
            response = simpleRestClient.convertTo(result, ConfirmDocumentCancelResponse.class);
        } catch (ServerCommunicationException exception) {
            // 결재 서버에는 이미 취소 반영이 되었지만 그룹웨어 쪽에서 취소 반영이 안된 경우
            // CD03:결재문서의 상태가 이미 취소 상태여서 수정하지 않는다는 응답
            if ("CD03".equals(exception.getErrorCode())) {
                log.warn("해당 문서는 이미 결재 서버에서 취소 처리된 문서로 그룹웨어의 휴가 상태만 취소 처리합니다.");
                return new ConfirmDocumentCancelResponse(confirmDocumentId, requesterId, ConfirmStatus.CANCEL);
            }
            // CD04 : 취소할 수 없는 상태의 결재 문서
            else if ("CD04".equals(exception.getErrorCode())) {
                String errMsg = "이미 상신이 올라간 결재 문서는 취소할 수 없습니다.";
                log.warn(errMsg);
                throw new VacationClientException(errMsg, requesterId);
            }
            // 그외의 예상치 못한 에러 처리
            URI uri = uriComponents.toUri();
            RequestUri requestUri = new RequestUri(uri.getHost(), uri.getPort(), uri.getPath());
            throw new ServerCommunicationException(exception.getStatusCode(), exception.getMessage(), requestUri, exception);

        } catch (JsonProcessingException e) {
            log.warn("JSON 파싱 과정에서 에러가 발생하여 강제로 문자열을 객체로 변환합니다.");
            return new ConfirmDocumentCancelResponse(confirmDocumentId, requesterId, ConfirmStatus.CANCEL);
        }

        return response;
    }
}
