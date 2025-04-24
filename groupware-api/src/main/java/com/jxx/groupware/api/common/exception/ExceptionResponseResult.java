package com.jxx.groupware.api.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * common 외부에서도 사용하는 클래스 - 퍼블릭
 */
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponseResult<T> {
    private Integer httpStatus;
    private T data;
}
