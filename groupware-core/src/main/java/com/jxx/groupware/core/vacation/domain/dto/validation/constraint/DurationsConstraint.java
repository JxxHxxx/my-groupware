package com.jxx.groupware.core.vacation.domain.dto.validation.constraint;

import jakarta.validation.Constraint;

import com.jxx.groupware.core.vacation.domain.dto.validation.validator.DurationsValidator;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationsValidator.class)
@Documented
public @interface DurationsConstraint {
    String message() default "시작일은 종료일보다 이후일 수 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
