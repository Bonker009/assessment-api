package com.kshrd.assessment.dto.exam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ExamResponse(
        UUID assessmentId,
        String name,
        Boolean isQuiz,
        UUID subjectId,
        LocalDate assessmentDate,
        LocalTime startTime,
        LocalTime endTime,
        Boolean isPublished,
        UUID createdBy,
        UUID updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
