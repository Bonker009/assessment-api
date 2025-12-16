package com.kshrd.assessment.dto.studentassessment;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StudentAssessmentRequest(
        @NotNull(message = "Assessment ID cannot be null")
        UUID assessmentId
) {
}
