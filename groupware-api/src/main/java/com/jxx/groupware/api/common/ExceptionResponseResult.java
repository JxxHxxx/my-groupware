package com.jxx.groupware.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponseResult {
    private Integer status;
    private ExceptionCommonResponse data;
}
