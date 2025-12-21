package com.kshrd.assessment.dto.studentassessment;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StudentAssessmentRequest(
        @NotNull(message = "Student ID cannot be null")
        UUID studentId,
        
        @NotNull(message = "Assessment ID cannot be null")
        UUID assessmentId
) {
}
