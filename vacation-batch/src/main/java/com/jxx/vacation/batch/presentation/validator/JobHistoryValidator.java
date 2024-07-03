package com.jxx.vacation.batch.presentation.validator;


import com.jxx.vacation.batch.dto.request.JobHistoryCond;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.MessageInterpolator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
public class JobHistoryValidator implements ConstraintValidator<DateDurationConstraint, JobHistoryCond> {

    private final static String DATE_FORMAT = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])";
    private final static String DATE_VIOLATION_MSG = "시작일과 종료일은 yyyy-MM-dd 포멧을 따라야 하며 정상적인 일자여야 합니다.";


    @Override
    public boolean isValid(JobHistoryCond cond, ConstraintValidatorContext context) {
        String strEndDate = cond.getEndDate();
        String strStartDate = cond.getStartDate();
        if (!strEndDate.matches(DATE_FORMAT) || strStartDate.matches(DATE_FORMAT)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(DATE_VIOLATION_MSG)
                    .addConstraintViolation();
            return false;
        }

        LocalDate endDate = LocalDate.parse(cond.getEndDate());
        LocalDate startDate = LocalDate.parse(cond.getStartDate());
        return startDate.isBefore(endDate);
    }
}
