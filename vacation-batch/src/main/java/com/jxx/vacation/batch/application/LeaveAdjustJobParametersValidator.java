package com.jxx.vacation.batch.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service("leave.adjust.job.parameter-validator")
public class LeaveAdjustJobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        log.info("valid leave adjust job");
    }
}
