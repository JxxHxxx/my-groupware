package com.jxx.groupware.batch.dto.request;

import com.jxx.groupware.batch.presentation.validator.DateDurationConstraint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@RequiredArgsConstructor
@DateDurationConstraint
public class JobHistoryCond {
    private final String startDate;
    private final String endDate;
}
