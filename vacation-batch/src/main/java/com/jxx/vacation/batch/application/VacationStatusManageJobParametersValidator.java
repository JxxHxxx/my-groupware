package com.jxx.vacation.batch.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service("vacation.status-manage.job.parameter-validator")
public class VacationStatusManageJobParametersValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        log.info("valid vacation status job");

    }
}
