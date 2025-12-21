package com.kshrd.assessment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ScheduleTimeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScheduleTime {
    String message() default "startTime must be before endtime";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
