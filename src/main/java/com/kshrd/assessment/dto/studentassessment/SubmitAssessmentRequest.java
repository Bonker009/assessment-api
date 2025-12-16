package com.kshrd.assessment.dto.studentassessment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SubmitAssessmentRequest(
        @NotNull(message = "Assessment ID cannot be null")
        java.util.UUID assessmentId,
        
        @NotNull(message = "Score cannot be null")
        @PositiveOrZero(message = "Score must be positive or zero")
        Double score,
        
        @NotNull(message = "Duration cannot be null")
        @PositiveOrZero(message = "Duration must be positive or zero")
        Double durationInMinute
) {
}
