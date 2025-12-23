package com.kshrd.assessment.dto.classroom;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AssignAssessmentToClassroomRequest(
        @NotNull(message = "Assessment IDs cannot be null")
        List<UUID> assessmentIds
) {
}

