package com.jxx.groupware.core.vacation.domain.dto.validation.validator;

import com.jxx.groupware.core.vacation.domain.dto.RequestVacationDuration;
import com.jxx.groupware.core.vacation.domain.dto.validation.constraint.DurationsConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DurationsValidator implements ConstraintValidator<DurationsConstraint, List<RequestVacationDuration>> {

    @Override
    public boolean isValid(List<RequestVacationDuration> durations, ConstraintValidatorContext context) {
        for (RequestVacationDuration duration : durations) {
            LocalDateTime startDateTime = duration.getStartDateTime();
            LocalDateTime endDateTime = duration.getEndDateTime();
            if (Objects.isNull(startDateTime) || Objects.isNull(endDateTime)) {
                context.buildConstraintViolationWithTemplate("시작일/종료일은 NULL 일 수 없습니다")
                        .addConstraintViolation();
                return false;
            }

            if (!(startDateTime.isBefore(endDateTime) || startDateTime.isEqual(endDateTime))) {
                context.disableDefaultConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
