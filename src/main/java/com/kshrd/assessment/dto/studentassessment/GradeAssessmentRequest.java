package com.kshrd.assessment.dto.studentassessment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record GradeAssessmentRequest(
        @NotNull(message = "Student ID cannot be null")
        java.util.UUID studentId,
        
        @NotNull(message = "Assessment ID cannot be null")
        java.util.UUID assessmentId,
        
        @NotNull(message = "Score cannot be null")
        @PositiveOrZero(message = "Score must be positive or zero")
        Double score,
        
        @NotBlank(message = "Grading status cannot be blank")
        String gradingStatus
) {
}
