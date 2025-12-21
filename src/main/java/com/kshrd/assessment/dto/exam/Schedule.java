package com.kshrd.assessment.dto.exam;

import com.kshrd.assessment.validation.ValidScheduleTime;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@ValidScheduleTime
public record Schedule(
        @NotNull
        LocalDate assessmentDate,
        @NotNull
        LocalTime startTime,
        @NotNull
        LocalTime endTime,
        @NotNull
        Boolean isPublished
) {
}

