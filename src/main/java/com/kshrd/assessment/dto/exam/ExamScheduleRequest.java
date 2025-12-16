package com.kshrd.assessment.dto.exam;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record ExamScheduleRequest(
        @NotNull(message = "Assessment date cannot be null")
        LocalDate assessmentDate,
        
        @NotNull(message = "Start time cannot be null")
        LocalTime startTime,
        
        @NotNull(message = "End time cannot be null")
        LocalTime endTime
) {
}
