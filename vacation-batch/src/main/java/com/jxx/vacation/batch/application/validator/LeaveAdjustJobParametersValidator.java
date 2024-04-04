package com.jxx.vacation.batch.application.validator;

import com.jxx.vacation.batch.job.parameters.JxxJobParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service("leave.adjust.job.parameter-validator")
public class LeaveAdjustJobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        log.info("start leave.adjust.job parameter validate");
        List<String> requiredParameterKeyNames = JxxJobParameter.LEAVE_ADJUST_REQUIRED_GROUP;

        List<String> notPreparedRequiredParams = requiredParameterKeyNames.stream()
                .filter(keyName -> Objects.isNull(parameters.getParameter(keyName)))
                .toList();

        if (!notPreparedRequiredParams.isEmpty()) {
            String message = String.format("필수 잡 파라미터 %s 가 존재하지 않습니다.", notPreparedRequiredParams);
            log.error("leave.adjust.job required parameter's {} not prepared", notPreparedRequiredParams);
            throw new JobParametersInvalidException(message);
        }
    }
}
