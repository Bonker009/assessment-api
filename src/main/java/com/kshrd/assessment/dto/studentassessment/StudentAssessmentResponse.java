package com.kshrd.assessment.dto.studentassessment;

import com.kshrd.assessment.utils.enums.Status;
import java.time.LocalDateTime;
import java.util.UUID;

public record StudentAssessmentResponse(
        UUID studentId,
        UUID assessmentId,
        Double score,
        Status status,
        LocalDateTime joinAt,
        Double durationInMinute,
        LocalDateTime submittedAt,
        String gradingStatus,
        Double totalScore
) {
}
