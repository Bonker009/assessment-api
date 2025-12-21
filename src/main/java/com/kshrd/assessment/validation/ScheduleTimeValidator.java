package com.kshrd.assessment.validation;

import com.kshrd.assessment.dto.exam.Schedule;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;

public class ScheduleTimeValidator implements ConstraintValidator<ValidScheduleTime, Schedule> {
    @Override
    public boolean isValid(Schedule schedule, ConstraintValidatorContext context) {
        if (schedule == null) return true;
        LocalTime start = schedule.startTime();
        LocalTime end = schedule.endTime();
        if (start == null || end == null) return true;
        return start.isBefore(end);
    }
}
