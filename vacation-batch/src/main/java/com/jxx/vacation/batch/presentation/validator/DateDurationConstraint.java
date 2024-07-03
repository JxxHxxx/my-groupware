package com.jxx.vacation.batch.presentation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JobHistoryValidator.class)
@Documented
public @interface DateDurationConstraint {
    String message() default "시작일은 종료일보다 이전이여야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
