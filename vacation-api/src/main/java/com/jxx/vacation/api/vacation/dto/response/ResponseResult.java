package com.jxx.vacation.api.vacation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class ResponseResult<T> {
    private Integer status;
    private String message;
    private T response;

    public ResponseResult(Integer status, String message, T response) {
        this.status = status;
        this.message = message;
        this.response = response;
    }
}
