package com.jxx.vacation.batch.application.validator;

import com.jxx.vacation.batch.exception.JxxJobExecutionException;
import com.jxx.vacation.batch.job.parameters.JxxJobParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * DB에서 조회하는 방식으로 추후 변경
 */
@Slf4j
@Service("vacation.end.job.parameter-validator")
public class VacationEndEventJobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) {
        log.info("start leave.adjust.job parameter validate");
        List<String> requiredParameterKeyNames = JxxJobParameter.LEAVE_ADJUST_REQUIRED_GROUP;

        /** 필수 파라미터 존재 여부 검증 START **/
        List<String> nonPreparedRequiredParams = requiredParameterKeyNames.stream()
                .filter(keyName -> Objects.isNull(parameters.getParameter(keyName)))
                .toList();

        if (!nonPreparedRequiredParams.isEmpty()) {
            String message = String.format("필수 잡 파라미터 %s 가 존재하지 않습니다.", nonPreparedRequiredParams);
            log.error("leave.adjust.job required parameter's {} not prepared", nonPreparedRequiredParams);
            throw new JxxJobExecutionException(message);
        }
        /** 필수 파라미터 존재 여부 검증 END **/
    }
}
