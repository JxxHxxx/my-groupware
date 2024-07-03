package com.jxx.vacation.batch.dto.request;

import com.jxx.vacation.batch.presentation.validator.DateDurationConstraint;
import jakarta.validation.constraints.Pattern;
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
