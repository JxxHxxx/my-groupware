package com.jxx.groupware.api.messaging.application;


import com.jxx.groupware.api.common.AdminResponseCode;
import com.jxx.groupware.api.common.ExceptionCommonResponse;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class MessageAdminException extends RuntimeException {
    private final String code;
    private final String applicationDomain;
    private final String description;

    public MessageAdminException(AdminResponseCode responseCode) {
        super(responseCode.getDescription());
        this.code = responseCode.getCode();
        this.applicationDomain = responseCode.getApplicationDomain();
        this.description = responseCode.getDescription();
    }

    public ExceptionCommonResponse toExceptionCommonResponse() {
        return new ExceptionCommonResponse(code, applicationDomain, description);
    }
}
