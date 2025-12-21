package com.kshrd.assessment.dto.exam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ExamResponse(
        UUID assessmentId,
        String name,
        Boolean isQuiz,
        UUID subjectId,
        Schedule schedule,
        UUID createdBy,
        UUID updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long totalSections,
        Long totalQuestions,
        List<SectionResponse> sections
) {
}
